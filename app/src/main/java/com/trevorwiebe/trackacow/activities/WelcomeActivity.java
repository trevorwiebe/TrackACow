package com.trevorwiebe.trackacow.activities;

import android.content.Intent;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.trevorwiebe.trackacow.R;

public class WelcomeActivity extends com.stephentuso.welcome.WelcomeActivity {


    @Override
    protected WelcomeConfiguration configuration() {

        BasicPage welcomePage = new BasicPage(R.drawable.cow_head, "Track-A-Cow", "Feedlot management tool. Designed with commercial feedlots in mind.");

        BasicPage medicateCows = new BasicPage(R.drawable.ic_drugs_white_24dp, "Monitor medication given", "You can easily keep track of and view medication given to a specific cow or pen of cattle.")
                .background(R.color.colorAccent);

        BasicPage headDays = new BasicPage(R.drawable.ic_add_white_24dp, "Calculates days per head", "Track-A-Cow will calculate the total days each cow has been in you feedlot.  Using this information you can guarantee accurate billing for your customers.")
                .background(R.color.colorPrimary);

        BasicPage deadCows = new BasicPage(R.drawable.ic_remove_circle_outline_white_24dp, "Keep track of dead cows", "Keeping track of dead cows is made easy with Track-A-Cow.  With this information your death loss percentage can be calculated")
                .background(R.color.welcome_orange);

        BasicPage cloudStorage = new BasicPage(R.drawable.ic_cloud_white_24dp, "Data stored in the cloud", "Your data is automatically stored securely in the cloud, this allows you to view all your information on multiple devices.  Data also caches right on each signed in device, so you never have to worry about having an active internet connection.  Track-A-Cow will sync back to the cloud once your connection is regained.")
                .background(R.color.welcome_purple);

        BasicPage subscriptionPage = new BasicPage(R.drawable.ic_money_white_24dp, "Free trial", "Track-A-Cow starts out with a 30 day free trial, after that we charge a monthly or annually subscription.")
                .background(R.color.welcome_green);

        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.welcome_blue)
                .page(welcomePage)
                .page(medicateCows)
                .page(headDays)
                .page(deadCows)
                .page(cloudStorage)
                .page(subscriptionPage)
                .canSkip(false)
                .bottomLayout(WelcomeConfiguration.BottomLayout.BUTTON_BAR)
                .backButtonNavigatesPages(false)
                .useCustomDoneButton(true)
                .build();
    }

    @Override
    protected void onButtonBarFirstPressed() {
        super.onButtonBarFirstPressed();
        Intent signInIntent = new Intent(WelcomeActivity.this, SignInActivity.class);
        startActivity(signInIntent);
    }

    @Override
    protected void onButtonBarSecondPressed() {
        super.onButtonBarSecondPressed();
        Intent createAccountIntent = new Intent(WelcomeActivity.this, CreateAccountActivity.class);
        startActivity(createAccountIntent);
    }
}
