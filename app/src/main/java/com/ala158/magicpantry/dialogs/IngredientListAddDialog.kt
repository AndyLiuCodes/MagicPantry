package com.ala158.magicpantry.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.ui.manualingredientinput.ManualIngredientInputActivity
import com.google.android.material.textfield.TextInputEditText

class IngredientListAddDialog : DialogFragment(), DialogInterface.OnClickListener {
    private lateinit var dialogView: View
    internal lateinit var ingredientListAddDialogListener: IngredientListAddDialogListener
    private lateinit var nameInputTextInputEditText: TextInputEditText
    private lateinit var unitInputSpinner: Spinner

    interface IngredientListAddDialogListener {
        fun onIngredientListAddDialogClick(newIngredientName: String, newIngredientUnit: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = requireActivity().layoutInflater.inflate(
            R.layout.dialog_ingredient_list_add,
            null
        )
        nameInputTextInputEditText = dialogView.findViewById(R.id.ingredient_list_add_name)
        unitInputSpinner = dialogView.findViewById(R.id.ingredient_list_add_unit)

        val unitAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.unit_items,
            R.layout.spinner_item_unit_dropdown
        )

        unitAdapter.setDropDownViewResource(R.layout.spinner_item_unit_dropdown)
        unitInputSpinner.adapter = unitAdapter
        // Default unit is unit
        unitInputSpinner.setSelection(ManualIngredientInputActivity.UNIT_DROPDOWN_MAPPING["unit"]!!)

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(R.string.ingredient_add_dialog_title)
        dialogBuilder.setPositiveButton(R.string.ingredient_add_dialog_btn_add, this)
        dialogBuilder.setNegativeButton(R.string.ingredient_add_dialog_btn_cancel, this)

        val builtDialog = dialogBuilder.create()

        return builtDialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Attaches the listener method created in IngredientListAddActivity
        ingredientListAddDialogListener = context as IngredientListAddDialogListener
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            // Error check
            val newIngredientName = nameInputTextInputEditText.text.toString()
            val newIngredientUnit = unitInputSpinner.selectedItem.toString()

            if (newIngredientName == "") {
                Toast.makeText(requireActivity(), "Ingredient is not added: Name cannot be empty", Toast.LENGTH_SHORT).show()
                dismiss()
                return
            }

            ingredientListAddDialogListener.onIngredientListAddDialogClick(newIngredientName, newIngredientUnit)
        } else {
            dismiss()
        }
    }
}