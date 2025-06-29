# SysApp: System App Test Utility

SysApp is an Android application designed for developers, ROM customizers, and power users to test and verify system-level configurations and permissions on an Android device.

## Features

- **Application Signature Verification:** Displays the application's digital signature details, including the issuer, subject, and validity period, to confirm its integrity.
- **Permission Status Checker:** Checks the status of critical system-level permissions, such as `WRITE_SECURE_SETTINGS`, `READ_LOGS`, and `ACCESS_SUPERUSER`, providing a quick overview of the app's privilege level.
- **System Settings Test:** Attempts to modify a secure system setting to test whether the `WRITE_SECURE_SETTINGS` permission is functional.
- **System Properties Test:** Reads various system properties and attempts to write a new one to test the device's property space.
- **Settings Reset:** Resets common system settings to their default values.

## Permissions

This application requests several powerful permissions to perform its functions. These permissions are intended for testing and diagnostic purposes only.

- `android.permission.WRITE_SECURE_SETTINGS`: Allows the app to read and write to the system's secure settings.
- `android.permission.READ_LOGS`: Allows the app to read the system's log files.
- `android.permission.ACCESS_SUPERUSER`: Allows the app to request root access.

## Disclaimer

This application is a developer tool and is not intended for general use. Modifying system settings can have unintended consequences, so please use this application with caution.
