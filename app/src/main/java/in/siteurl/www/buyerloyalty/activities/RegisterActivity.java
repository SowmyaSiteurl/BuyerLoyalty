package in.siteurl.www.buyerloyalty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import in.siteurl.www.buyerloyalty.R;


public class RegisterActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPhone, editTextPassword, editTextAddress;
    TextInputLayout textInputLayoutName, textInputLayoutEmail, textInputLayoutPhone, textInputLayoutPassword, textInputLayoutaddress;
    CheckBox checkBox;
    String str_email, str_uname, str_phone, str_address, str_password;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Toolbar mToolbar;
    private TextView toolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //UI elements to get the ID
        editTextName = findViewById(R.id.rnameEdittext);
        editTextEmail = findViewById(R.id.remailEdittext);
        editTextPhone = findViewById(R.id.rphoneEdittext);
        editTextPassword = findViewById(R.id.rpasswordEdittext);
        editTextAddress = findViewById(R.id.raddressEdittext);
        textInputLayoutName = findViewById(R.id.rnameTextinputlayout);
        textInputLayoutEmail = findViewById(R.id.remailTextinputlayout);
        textInputLayoutPhone = findViewById(R.id.rphoneTextinputlayout);
        textInputLayoutPassword = findViewById(R.id.rpasswordTextinputlayout);
        textInputLayoutaddress = findViewById(R.id.raddressTextinputlayout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Register Here");

        checkBox = (CheckBox) findViewById(R.id.rShowPwd);

        //Check box to show the password
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }


    public void continueRegister(View view) {
        str_email = editTextEmail.getText().toString().trim();
        str_uname = editTextName.getText().toString().trim();
        str_phone = editTextPhone.getText().toString().trim();
        str_password = editTextPassword.getText().toString().trim();
        str_address = editTextAddress.getText().toString().trim();
        validate();
    }

    //this is the method for validations
    public void validate() {
        if (str_uname.equals("") || (str_uname.equals(null))) {
            textInputLayoutName.setError("Enter a valid name");
            return;
        } else {
            textInputLayoutName.setError(null);
        }

        if (str_email.equals("") || (str_email.equals(null) || !(str_email.matches(EMAIL_PATTERN)))) {
            textInputLayoutEmail.setError("Enter a valid email address");
            return;
        } else {
            textInputLayoutEmail.setError(null);
        }

        if (str_phone.equals("") || (str_phone.equals(null)) || (str_phone.length() != 10)) {
            textInputLayoutPhone.setError("Enter a valid phone");
            return;
        } else {
            textInputLayoutPhone.setError(null);
        }

        if (str_address.equals("") || (str_address.equals(null))) {
            textInputLayoutaddress.setError("Enter a valid address");
            return;
        } else {
            textInputLayoutaddress.setError(null);
        }

        if (str_password.equals("") || (str_password.equals(null))) {
            textInputLayoutPassword.setError("Enter a valid password");
            return;
        } else {

            textInputLayoutPassword.setError(null);

            startActivity(new Intent(RegisterActivity.this, ConfirmPassword.class)
                    .putExtra("uname", str_uname)
                    .putExtra("email", str_email)
                    .putExtra("phone", str_phone)
                    .putExtra("address", str_address)
                    .putExtra("password", str_password)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    public void goToLoginPage(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
