package budgetapp.main.test.robotium;

import com.robotium.solo.Solo;

import budgetapp.activities.MainActivity;
import budgetapp.main.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainView {

	private MainActivity activity;
	private Solo solo;
	private EditText subtractionEditText;
	private TextView currentBudgetTextView;
	private Button categorySpinner;
	
	public MainView(MainActivity activity, Solo solo) {
		this.activity = activity;
		this.solo = solo;
		setUpViews();
	}
	
	public EditText getSubtractionEditText() {
		return subtractionEditText;
	}
	
	public TextView getCurrentBudgetTextView() {
		return currentBudgetTextView;
	}
	
	public Button getCategorySpinner() {
		return categorySpinner;
	}
	
	private void setUpViews() {
		subtractionEditText = (EditText) activity.findViewById(R.id.editTextSubtract);
		currentBudgetTextView = (TextView) activity.findViewById(R.id.textViewCurrentBudget);
		categorySpinner = (Button) activity.findViewById(R.id.button_choose_category);
	}
}
