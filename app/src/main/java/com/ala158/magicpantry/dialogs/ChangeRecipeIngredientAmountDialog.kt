package com.ala158.magicpantry.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.ala158.magicpantry.R
import com.google.android.material.textfield.TextInputEditText

class ChangeRecipeIngredientAmountDialog(private val changeRecipeIngredientAmountDialogListener: ChangeRecipeIngredientAmountDialogListener) :
    DialogFragment(),
    DialogInterface.OnClickListener {
    private lateinit var dialogView: View

    interface ChangeRecipeIngredientAmountDialogListener {
        fun onChangeRecipeIngredientAmountConfirm(amount: Double)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = requireActivity().layoutInflater.inflate(
            R.layout.dialog_change_recipe_item_amount,
            null
        )

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Change Ingredient Amount")

        dialogBuilder.setPositiveButton(R.string.dialog_save_button_text, this)
        dialogBuilder.setNegativeButton(R.string.dialog_cancel_button_text, this)

        val dialog = dialogBuilder.create()

        return dialog
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            val amountView = dialogView.findViewById<TextInputEditText>(R.id.change_recipe_item_amount)

            if (amountView.text.toString() == "") {
                dismiss()
            } else {
                val amount = amountView.text.toString().toDouble()
                changeRecipeIngredientAmountDialogListener.onChangeRecipeIngredientAmountConfirm(amount)
            }

        } else {
            dismiss()
        }
    }
}