package com.ala158.magicpantry.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ala158.magicpantry.R

class AddMissingIngredientsToShoppingListDialog(private val isRecipe: Boolean = true) :
    DialogFragment(), DialogInterface.OnClickListener {
    private lateinit var dialogView: View
    internal lateinit var addMissingIngredientDialogListener: AddMissingIngredientDialogListener

    interface AddMissingIngredientDialogListener {
        fun onConfirmationClick(isConfirm: Boolean)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = requireActivity().layoutInflater.inflate(
            R.layout.dialog_add_missing_ingredients_to_shopping_list,
            null
        )
        val questionPrompt = dialogView.findViewById<TextView>(R.id.add_ingredients_shopping_list)
        if (isRecipe) {
            questionPrompt.text =
                getString(R.string.confirm_add_missing_ingredient_to_shopping_list_msg)
        } else {
            questionPrompt.text =
                getString(R.string.confirm_add_low_ingredients_to_shopping_list_msg)
        }

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(R.string.dialog_add_missing_ingredients_title)
        dialogBuilder.setPositiveButton(R.string.dialog_add_missing_ingredients_confirm, this)
        dialogBuilder.setNegativeButton(R.string.dialog_add_missing_ingredients_cancel, this)

        val builtDialog = dialogBuilder.create()

        return builtDialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Attaches the listener method created in the SingleRecipeActivity
        addMissingIngredientDialogListener = context as AddMissingIngredientDialogListener
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            addMissingIngredientDialogListener.onConfirmationClick(true)
        } else {
            dismiss()
        }
    }
}