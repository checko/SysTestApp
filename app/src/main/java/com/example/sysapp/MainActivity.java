package com.example.sysapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SysApp";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private TextView statusText;
    private TextView signatureText;
    private Button testSettingsButton;
    private Button testSystemPropsButton;
    private Button checkPermissionsButton;
    private Button resetSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        checkAppSignature();
        checkPermissions();
    }

    private void initializeViews() {
        statusText = findViewById(R.id.statusText);
        signatureText = findViewById(R.id.signatureText);
        testSettingsButton = findViewById(R.id.testSettingsButton);
        testSystemPropsButton = findViewById(R.id.testSystemPropsButton);
        checkPermissionsButton = findViewById(R.id.checkPermissionsButton);
        resetSettingsButton = findViewById(R.id.resetSettingsButton);

        testSettingsButton.setOnClickListener(v -> testSystemSettings());
        testSystemPropsButton.setOnClickListener(v -> testSystemProperties());
        checkPermissionsButton.setOnClickListener(v -> checkPermissions());
        resetSettingsButton.setOnClickListener(v -> resetSettings());
    }

    private void checkAppSignature() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                getPackageName(), PackageManager.GET_SIGNATURES);
            
            Signature[] signatures = packageInfo.signatures;
            if (signatures.length > 0) {
                Signature signature = signatures[0];
                String signatureInfo = "App Signature:\n" +
                    "Hash: " + signature.toCharsString() + "\n";
                
                // Try to get certificate details
                try {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(
                        new ByteArrayInputStream(signature.toByteArray()));
                    
                    signatureInfo += "Issuer: " + cert.getIssuerDN() + "\n";
                    signatureInfo += "Subject: " + cert.getSubjectDN() + "\n";
                    signatureInfo += "Valid From: " + new SimpleDateFormat("yyyy-MM-dd").format(cert.getNotBefore()) + "\n";
                    signatureInfo += "Valid Until: " + new SimpleDateFormat("yyyy-MM-dd").format(cert.getNotAfter());
                } catch (Exception e) {
                    signatureInfo += "Certificate parsing failed: " + e.getMessage();
                }
                
                signatureText.setText(signatureInfo);
            }
        } catch (Exception e) {
            signatureText.setText("Error reading signature: " + e.getMessage());
        }
    }

    private void checkPermissions() {
        StringBuilder status = new StringBuilder();
        status.append("Permission Status:\n\n");
        
        // Check platform permissions
        String[] platformPermissions = {
            Manifest.permission.WRITE_SECURE_SETTINGS,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.READ_LOGS,
            Manifest.permission.QUERY_ALL_PACKAGES
        };
        
        for (String permission : platformPermissions) {
            boolean granted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
            status.append(permission).append(": ").append(granted ? "✓ GRANTED" : "✗ DENIED").append("\n");
        }
        
        statusText.setText(status.toString());
    }

    private void testSystemSettings() {
        try {
            // Test writing to secure settings (platform-only permission)
            String originalValue = Settings.System.getString(getContentResolver(), 
                Settings.System.SCREEN_OFF_TIMEOUT);
            
            // Try to change screen timeout to 30 seconds
            boolean success = Settings.System.putString(getContentResolver(), 
                Settings.System.SCREEN_OFF_TIMEOUT, "30000");
            
            if (success) {
                Toast.makeText(this, "✓ Successfully modified secure settings!", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Successfully modified SCREEN_OFF_TIMEOUT to 30000");
                
                // Restore original value
                Settings.System.putString(getContentResolver(), 
                    Settings.System.SCREEN_OFF_TIMEOUT, originalValue);
            } else {
                Toast.makeText(this, "✗ Failed to modify secure settings", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to modify SCREEN_OFF_TIMEOUT");
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "✗ Security Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Security Exception when modifying settings", e);
        } catch (Exception e) {
            Toast.makeText(this, "✗ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error testing system settings", e);
        }
    }

    private void testSystemProperties() {
        try {
            StringBuilder result = new StringBuilder();
            result.append("System Properties Test:\n\n");
            
            // Test reading system properties
            String[] properties = {
                "ro.build.version.release",
                "ro.build.version.sdk",
                "ro.product.model",
                "ro.product.manufacturer",
                "ro.build.fingerprint"
            };
            
            for (String prop : properties) {
                String value = System.getProperty(prop, "Not accessible");
                result.append(prop).append(": ").append(value).append("\n");
            }
            
            // Try to set a system property (this should fail without root)
            try {
                System.setProperty("sysapp.test.property", "test_value");
                result.append("\nsysapp.test.property set successfully\n");
            } catch (SecurityException e) {
                result.append("\nsysapp.test.property: Cannot set (expected)\n");
            }
            
            Toast.makeText(this, "System properties test completed", Toast.LENGTH_SHORT).show();
            statusText.setText(result.toString());
            
        } catch (Exception e) {
            Toast.makeText(this, "Error testing system properties: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error testing system properties", e);
        }
    }

    private void resetSettings() {
        try {
            // Reset some common settings to default values
            Settings.System.putString(getContentResolver(), 
                Settings.System.SCREEN_OFF_TIMEOUT, "30000");
            Settings.System.putString(getContentResolver(), 
                Settings.System.SCREEN_BRIGHTNESS_MODE, "1");
            
            Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Settings reset completed");
        } catch (Exception e) {
            Toast.makeText(this, "Error resetting settings: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error resetting settings", e);
        }
    }
} 