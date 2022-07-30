package com.ala158.magicpantry.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ala158.magicpantry.R
import com.ala158.magicpantry.ui.pantry.PantryFragment
import com.google.android.material.textfield.TextInputEditText

class PantryAddShoppingListDialog : DialogFragment(), DialogInterface.OnClickListener {
    private lateinit var dialogView: View
    private lateinit var pantryAddShoppingListDialogListener: PantryAddShoppingListDialogListener
    private var relatedIngredientUnit = ""
    private var relatedIngredientName = ""
    private var relatedIngredientId = 0L

    interface PantryAddShoppingListDialogListener : Parcelable {
        fun onPantryAddShoppingListDialogClick(unit: String, name: String, id: Long, amount: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = requireActivity().layoutInflater.inflate(
            R.layout.dialog_add_to_shopping_list,
            null
        )

        val arg = arguments
        if (arg != null) {
            relatedIngredientUnit = arg.getString(
                PantryFragment.DIALOG_RELATED_INGREDIENT_UNIT_KEY, ""
            )
            relatedIngredientName = arg.getString(
                PantryFragment.DIALOG_RELATED_INGREDIENT_NAME_KEY, ""
            )
            relatedIngredientId = arg.getLong(PantryFragment.DIALOG_RELATED_INGREDIENT_ID_KEY)
            pantryAddShoppingListDialogListener = arg.getParcelable(
                PantryFragment.DIALOG_ADD_SHOPPING_LIST_LISTENER_KEY
            )!!
        }

        val unitTextView = dialogView.findViewById<TextView>(R.id.add_to_shopping_list_unit)
        unitTextView.text = relatedIngredientUnit

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(
            String.format(
                requireActivity().resources.getString(R.string.dialog_add_to_shopping_list_title),
                relatedIngredientName
            )
        )

        dialogBuilder.setPositiveButton(R.string.dialog_add_button_text, this)
        dialogBuilder.setNegativeButton(R.string.dialog_cancel_button_text, this)

        val dialog = dialogBuilder.create()

        return dialog
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            val amountView = dialogView.findViewById<TextInputEditText>(R.id.add_to_shopping_list_amount)
            var amount = 0

            if( amountView.text.toString() != "")
                amount = amountView.text.toString().toInt()

            pantryAddShoppingListDialogListener.onPantryAddShoppingListDialogClick(
                relatedIngredientUnit,
                relatedIngredientName,
                relatedIngredientId,
                amount
            )
        } else {
            dismiss()
        }
    }
}