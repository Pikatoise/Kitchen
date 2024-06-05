package com.example.kitchen.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kitchen.R
import com.example.kitchen.databinding.ActivityAddDishBinding
import com.example.kitchen.dtos.DishDto
import com.example.kitchen.dtos.IngredientDto
import com.example.kitchen.lists.IngredientsAdapter
import com.example.kitchen.models.Dish
import com.example.kitchen.models.DishCategory
import com.example.kitchen.models.Ingredient
import com.example.kitchen.sqlite.PreferencesRepository
import com.example.kitchen.supabase.SupabaseModule
import com.example.kitchen.supabase.interfaces.CategoryRepository
import com.example.kitchen.supabase.interfaces.DishRepository
import com.example.kitchen.supabase.interfaces.ImageRepository
import com.example.kitchen.supabase.interfaces.IngredientRepository
import com.example.kitchen.supabase.repositories.CategoryRepositoryImpl
import com.example.kitchen.supabase.repositories.DishRepositoryImpl
import com.example.kitchen.supabase.repositories.ImageRepositoryImpl
import com.example.kitchen.supabase.repositories.IngredientRepositoryImpl
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class AddDishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDishBinding

    private var image: Uri? = null
    private lateinit var mGetContent: ActivityResultLauncher<String>
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var imageRepository: ImageRepository
    private lateinit var dishRepository: DishRepository
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private var profileId = 0

    private var categories: List<DishCategory>? = null
    private var ingredients: MutableList<Ingredient> = mutableListOf()
    private var category: DishCategory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddDishBinding.inflate(layoutInflater)

        mGetContent =
            registerForActivityResult<String, Uri?>(ActivityResultContracts.GetContent()) {
                image = it
                binding.ivAddDishImage.setImageURI(it)
            }

        val adapter = IngredientsAdapter(this, ingredients)
        binding.lvAddDishIngredients.adapter = adapter
        justifyListViewHeightBasedOnChildren(binding.lvAddDishIngredients)

        val provider = SupabaseModule.provideSupabaseDatabase()
        val providerBucket = SupabaseModule.provideSupabaseStorage()
        imageRepository = ImageRepositoryImpl(providerBucket)
        dishRepository = DishRepositoryImpl(provider)
        ingredientRepository = IngredientRepositoryImpl(provider)
        categoryRepository = CategoryRepositoryImpl(provider)
        preferencesRepository = PreferencesRepository(this)

        profileId = preferencesRepository.getProfileId()
        if (profileId < 1)
            finish()

        lifecycleScope.launch {
            categories = categoryRepository.getAllCategories()
        }

        binding.ivAddDishExit.setOnClickListener {
            finish()
        }

        binding.mcvAddDishLoadPhoto.setOnClickListener {
            mGetContent.launch("image/*")
        }

        binding.tvAddDishCookingTimeMinus.setOnClickListener {
            var cookingTime = binding.etAddDishCookingTime.text.toString().toInt()

            if (cookingTime > 0)
                cookingTime -= 1
            else
                return@setOnClickListener

            binding.etAddDishCookingTime.setText(cookingTime.toString())
        }

        binding.tvAddDishCookingTimePlus.setOnClickListener {
            var cookingTime = binding.etAddDishCookingTime.text.toString().toInt()

            if (cookingTime < 99)
                cookingTime += 1
            else
                return@setOnClickListener

            binding.etAddDishCookingTime.setText(cookingTime.toString())
        }

        binding.tvAddDishPortionCountMinus.setOnClickListener {
            var portionCount = binding.etAddDishPortionCount.text.toString().toInt()

            if (portionCount > 0)
                portionCount -= 1
            else
                return@setOnClickListener

            binding.etAddDishPortionCount.setText(portionCount.toString())
        }

        binding.tvAddDishPortionCountPlus.setOnClickListener {
            var portionCount = binding.etAddDishPortionCount.text.toString().toInt()

            if (portionCount < 99)
                portionCount += 1
            else
                return@setOnClickListener

            binding.etAddDishPortionCount.setText(portionCount.toString())
        }

        binding.ivAddDishIngredientAdd.setOnClickListener {
            showIngredientDialog()
        }

        binding.lvAddDishIngredients.setOnItemClickListener { _, _, position, _ ->
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            ingredients.removeAt(position)

                            justifyListViewHeightBasedOnChildren(binding.lvAddDishIngredients)
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder
                .setMessage("Удалить ингредиент?")
                .setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener)
                .show()
        }

        binding.llAddDishCategoryContainer.setOnClickListener {
            if (categories != null)
                showCategoryDialog()
        }

        binding.ivAddDishSave.setOnClickListener {
            var name = binding.etAddDishName.text.toString().trim()
            var cookingTime = binding.etAddDishCookingTime.text.toString().toInt()
            var portionCount = binding.etAddDishPortionCount.text.toString().toInt()
            var recipe = binding.etAddDishRecipe.text.toString().trim()
            var imageBytes: ByteArray? = null

            if (image != null)
                imageBytes = readBytesFromUri(image!!)

            if (name.isBlank()){
                Toast.makeText(this, "Введите название", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if (name.length > 30){
                Toast.makeText(this, "Название слишком длинное", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if (cookingTime <= 0){
                Toast.makeText(this, "Установите время готовки", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if (portionCount <= 0){
                Toast.makeText(this, "Установите количество порций", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if (ingredients.isEmpty()){
                Toast.makeText(this, "Добавьте хотя бы один ингредиент", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if (category == null){
                Toast.makeText(this, "Выберите категорию", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            if (recipe.isBlank()){
                Toast.makeText(this, "Введите рецепт", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            val progressDialog = ProgressDialog.show(this, "", "")
            progressDialog.show()
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            var newDish: Dish? = null
            lifecycleScope.launch {
                newDish = dishRepository.addDish(DishDto(
                    name = name,
                    recipe = recipe,
                    cookingTime = cookingTime,
                    portionCount = portionCount,
                    categoryId = category!!.id,
                    profileId = profileId,
                    image = " "
                ))
            }.invokeOnCompletion {
                if (newDish == null){
                    progressDialog.dismiss()

                    Toast.makeText(this, "Ошибка. Повторите еще раз", Toast.LENGTH_SHORT).show()

                    return@invokeOnCompletion
                }

                var isImageLoaded = false
                var isImagePathSet = false
                var isIngredientsLoaded = false
                var loadedIngredientCount = 0
                val imagePath = "dish_${newDish!!.id}.jpg"

                if (imageBytes != null){
                    lifecycleScope.launch {
                        imageRepository.loadDishImage(imagePath, imageBytes)
                    }.invokeOnCompletion {
                        isImageLoaded = true

                        lifecycleScope.launch {
                            dishRepository.updateDishImage(newDish!!.id, imagePath)
                        }.invokeOnCompletion {
                            isImagePathSet = true

                            if (isImageLoaded && isIngredientsLoaded){
                                progressDialog.dismiss()

                                finish()
                            }
                        }
                    }
                }
                else{
                    isImageLoaded = true
                    isImagePathSet = true
                }

                ingredients.forEach {
                    lifecycleScope.launch {
                        ingredientRepository.addIngredient(IngredientDto(
                            name = it.name,
                            description = it.description,
                            dishId = newDish!!.id
                        ))
                    }.invokeOnCompletion {
                        loadedIngredientCount += 1

                        if (loadedIngredientCount == ingredients.count()){
                            isIngredientsLoaded = true

                            if (isImagePathSet && isImageLoaded){
                                progressDialog.dismiss()

                                finish()
                            }
                        }
                    }
                }
            }
        }

        setContentView(binding.root)
    }

    fun justifyListViewHeightBasedOnChildren(listView: ListView) {
        val adapter = listView.adapter ?: return

        val vg: ViewGroup = listView
        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, vg)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val par = listView.layoutParams
        par.height = totalHeight + (listView.dividerHeight * (adapter.count - 1))
        listView.layoutParams = par
        listView.requestLayout()
    }

    private fun readBytesFromUri(uri: Uri): ByteArray{
        var os = ByteArrayOutputStream()

        var inputStream = this.contentResolver.openInputStream(uri)

        var byteArray = inputStream!!.readBytes()

        inputStream.close()

        return byteArray
    }

    private fun showIngredientDialog(){
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_add_ingredient)

        val ivExit = dialog.findViewById<ImageView>(R.id.iv_dialog_add_ingredient_exit)
        val etName = dialog.findViewById<EditText>(R.id.et_dialog_add_ingredient_name)
        val etCount = dialog.findViewById<EditText>(R.id.et_dialog_add_ingredient_count)
        val mcvAdd = dialog.findViewById<MaterialCardView>(R.id.mcv_dialog_add_ingredient_add)

        ivExit.setOnClickListener {
            dialog.dismiss()
        }

        mcvAdd.setOnClickListener {
            var name = etName.text.toString().trim()
            var count = etCount.text.toString().trim()

            if (name.isBlank()){
                Toast.makeText(this,"Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (count.isBlank()){
                Toast.makeText(this,"Введите количество", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ingredients.add(Ingredient(-1, name, count, -1))

            justifyListViewHeightBasedOnChildren(binding.lvAddDishIngredients)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCategoryDialog(){
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_select_category)

        val ivExit = dialog.findViewById<ImageView>(R.id.iv_dialog_select_category_exit)
        val lvICategories = dialog.findViewById<ListView>(R.id.lv_dialog_select_category_categories)

        lvICategories.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            categories!!.map { x -> x.name })

        ivExit.setOnClickListener {
            dialog.dismiss()
        }

        lvICategories.setOnItemClickListener { _, _, position, _ ->
            category = categories!![position]

            binding.tvAddDishCategory.text = category!!.name

            dialog.dismiss()
        }

        dialog.show()
    }
}