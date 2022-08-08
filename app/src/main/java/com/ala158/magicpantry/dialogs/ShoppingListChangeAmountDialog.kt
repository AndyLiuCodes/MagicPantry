package com.ala158.magicpantry.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.ui.shoppinglist.ShoppingListFragment
import com.google.android.material.textfield.TextInputEditText

class ShoppingListChangeAmountDialog : DialogFragment(), DialogInterface.OnClickListener {
    private lateinit var dialogView: View
    private var shoppingListChangeAmountDialogListener: ShoppingListChangeAmountDialogListener? =
        null
    private var ingredientName = ""
    private var ingredientUnit = ""

    interface ShoppingListChangeAmountDialogListener : Parcelable {
        fun onShoppingListChangeAmountDialogClick(amount: Double)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = requireActivity().layoutInflater.inflate(
            R.layout.dialog_change_shopping_item_amount, null
        )

        val arg = arguments
        if (arg != null) {
            ingredientName = arg.getString(ShoppingListFragment.DIALOG_INGREDIENT_NAME_KEY, "")
            ingredientUnit = arg.getString(ShoppingListFragment.DIALOG_INGREDIENT_UNIT_KEY, "")
            shoppingListChangeAmountDialogListener =
                arg.getParcelable(ShoppingListFragment.DIALOG_SHOPPING_LIST_LISTENER_KEY)

        }

        val unitTextView = dialogView.findViewById<TextView>(R.id.change_shopping_item_unit)
        unitTextView.text = ingredientUnit

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(
            String.format(
                requireActivity().resources.getString(
                    R.string.dialog_change_shopping_item_amount_title,
                    ingredientName
                )
            )
        )

        dialogBuilder.setPositiveButton(R.string.dialog_save_button_text, this)
        dialogBuilder.setNegativeButton(R.string.dialog_cancel_button_text, this)

        val dialog = dialogBuilder.create()

        return dialog
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            val amountView =
                dialogView.findViewById<TextInputEditText>(R.id.change_shopping_item_amount)

            if (amountView.text.toString() == "") {
                dismiss()
            } else {
                val amount = amountView.text.toString().toDouble()

                if (shoppingListChangeAmountDialogListener != null) {
                    shoppingListChangeAmountDialogListener!!.onShoppingListChangeAmountDialogClick(
                        amount
                    )
                }
            }
        } else {
            dismiss()
        }
    }
}