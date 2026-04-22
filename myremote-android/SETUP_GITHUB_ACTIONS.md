# GitHub Actions Setup Guide

This guide explains how to configure GitHub Actions to build APKs automatically.

## Workflows Included

### 1. Build Android APK (`build.yml`)
- **Triggers**: Push to main/master, PRs, or manual trigger
- **Outputs**: 
  - Debug APK (installable on test devices)
  - Release APK (unsigned)

### 2. Build Signed Release APK (`build-signed.yml`)
- **Triggers**: Manual trigger only (workflow_dispatch)
- **Inputs**: Version name
- **Outputs**: Signed release APK ready for distribution
- **Requires**: GitHub Secrets configuration

## Required GitHub Secrets

For signed builds, add these secrets in your repository settings:

1. **GOOGLE_SERVICES_JSON** (optional but recommended)
   - Your actual Firebase `google-services.json` content
   
2. **RELEASE_KEYSTORE_BASE64** (required for signed builds)
   - Your keystore file encoded in base64:
     ```bash
     base64 -w0 your-keystore.jks
     ```

3. **KEYSTORE_PASSWORD**
   - Your keystore password

4. **KEY_ALIAS**
   - Your key alias name

5. **KEY_PASSWORD**
   - Your key password

## How to Add Secrets

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret with its name and value

## How to Trigger Builds

### Automatic Build (Debug + Unsigned Release)
- Push code to `main` or `master` branch
- Create a pull request

### Manual Build (Debug + Unsigned Release)
1. Go to **Actions** tab
2. Select **Build Android APK** workflow
3. Click **Run workflow**
4. Choose branch and click **Run workflow**
5. Download APK from the workflow run

### Manual Signed Build
1. Configure all required secrets first
2. Go to **Actions** tab
3. Select **Build Signed Release APK** workflow
4. Click **Run workflow**
5. Enter version name (e.g., `1.0.0`)
6. Click **Run workflow**
7. Download signed APK from the workflow run

## First Time Setup

1. **Create Firebase Project**
   - Go to https://console.firebase.google.com
   - Create a new project
   - Add Android app with package name: `com.myremote.admin`
   - Download `google-services.json`

2. **Generate Keystore** (for signed releases)
   ```bash
   keytool -genkey -v -keystore myremote-release.jks \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias myremote
   ```

3. **Add Secrets to GitHub**
   - Follow the steps above to add all required secrets

4. **Push Code to GitHub**
   ```bash
   git add .
   git commit -m "Setup GitHub Actions for APK builds"
   git push origin main
   ```

## Troubleshooting

### Build Fails with "google-services.json not found"
- The workflow creates a placeholder automatically
- For real builds, add the `GOOGLE_SERVICES_JSON` secret

### Signed Build Fails
- Verify all 5 secrets are correctly set
- Ensure keystore base64 encoding is correct
- Check that passwords match

### Gradle Issues
- The workflow uses `--no-daemon` flag for CI stability
- JDK 17 and Android SDK are auto-configured

## Artifacts

Built APKs are available for 30-90 days:
- Go to the workflow run
- Scroll to **Artifacts** section
- Click to download

