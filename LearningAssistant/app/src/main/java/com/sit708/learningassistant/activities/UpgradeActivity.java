package com.sit708.learningassistant.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.material.button.MaterialButton;
import com.sit708.learningassistant.R;
import com.sit708.learningassistant.models.AppDatabase;
import com.sit708.learningassistant.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Task 10.1 – New screen.
 * Three subscription tiers: Starter ($4.99), Intermediate ($9.99), Advanced ($19.99).
 * Uses Google Pay API for payment. On success, updates the user's tier in Room DB.
 */
public class UpgradeActivity extends AppCompatActivity {

    private PaymentsClient paymentsClient;
    private String pendingTier;
    private String pendingPrice;

    private SessionManager session;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Google Pay result launcher
    private final ActivityResultLauncher<IntentSenderRequest> googlePayLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    this::handleGooglePayResult
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        session = new SessionManager(this);
        db      = AppDatabase.getInstance(this);

        // Initialise Google Pay client in TEST environment
        paymentsClient = Wallet.getPaymentsClient(
                this,
                new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build()
        );

        MaterialButton btnStarter      = findViewById(R.id.btnBuyStarter);
        MaterialButton btnIntermediate = findViewById(R.id.btnBuyIntermediate);
        MaterialButton btnAdvanced     = findViewById(R.id.btnBuyAdvanced);

        btnStarter.setOnClickListener(v      -> requestGooglePay("Starter",      "4.99"));
        btnIntermediate.setOnClickListener(v -> requestGooglePay("Intermediate", "9.99"));
        btnAdvanced.setOnClickListener(v     -> requestGooglePay("Advanced",     "19.99"));
    }

    // ── Google Pay request ────────────────────────────────────────────────────

    private void requestGooglePay(String tier, String price) {
        pendingTier  = tier;
        pendingPrice = price;

        try {
            JSONObject paymentDataRequest = buildPaymentDataRequest(price);
            PaymentDataRequest request =
                    PaymentDataRequest.fromJson(paymentDataRequest.toString());

            Task<PaymentData> task = paymentsClient.loadPaymentData(request);
            task.addOnCompleteListener(completedTask -> {
                if (completedTask.isSuccessful()) {
                    onPaymentSuccess(completedTask.getResult());
                } else {
                    Exception e = completedTask.getException();
                    if (e instanceof ResolvableApiException) {
                        // Google Pay sheet needs to be shown
                        try {
                            IntentSenderRequest intentSenderRequest =
                                    new IntentSenderRequest.Builder(
                                            ((ResolvableApiException) e).getResolution()
                                    ).build();
                            googlePayLauncher.launch(intentSenderRequest);
                        } catch (Exception ex) {
                            Toast.makeText(this, "Could not launch Google Pay", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Google Pay not available on this device",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (JSONException e) {
            Toast.makeText(this, "Payment request error", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject buildPaymentDataRequest(String price) throws JSONException {
        JSONObject cardParams = new JSONObject()
                .put("allowedAuthMethods", new JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS"))
                .put("allowedCardNetworks",
                        new JSONArray().put("AMEX").put("MASTERCARD").put("VISA"));

        JSONObject paymentMethod = new JSONObject()
                .put("type", "CARD")
                .put("parameters", cardParams);

        JSONObject transactionInfo = new JSONObject()
                .put("totalPrice", price)
                .put("totalPriceStatus", "FINAL")
                .put("currencyCode", "AUD");

        JSONObject merchantInfo = new JSONObject()
                .put("merchantName", "LearnAI SIT708");

        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0)
                .put("allowedPaymentMethods", new JSONArray().put(paymentMethod))
                .put("transactionInfo", transactionInfo)
                .put("merchantInfo", merchantInfo);
    }

    // ── Result handling ───────────────────────────────────────────────────────

    private void handleGooglePayResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            PaymentData paymentData = PaymentData.getFromIntent(result.getData());
            onPaymentSuccess(paymentData);
        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void onPaymentSuccess(PaymentData paymentData) {
        // In production you would send paymentData.toJson() to your server.
        // Here we simply update the tier locally (TEST environment).
        String tier = pendingTier;
        String username = session.getUsername();

        executor.execute(() -> {
            db.userDao().updateTier(username, tier);
            handler.post(() -> {
                session.saveTier(tier);
                Toast.makeText(this,
                        "🎉 Upgraded to " + tier + "! Enjoy your new features.",
                        Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
