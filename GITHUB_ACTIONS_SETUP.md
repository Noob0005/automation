# GitHub Actions APK Build Setup - Complete Guide

## ✅ What's Been Configured

Your Android project now has complete GitHub Actions CI/CD setup with:

### Two Automated Workflows

1. **`build.yml`** - Automatic builds on every push/PR
   - Builds Debug APK (installable for testing)
   - Builds Release APK (unsigned)
   - Triggers: Push to main/master, PRs, or manual

2. **`build-signed.yml`** - Manual signed release builds
   - Builds production-ready signed APK
   - Requires GitHub secrets configuration
   - Triggered manually with version input

## 🚀 Quick Start - 5 Minutes to First APK

### Step 1: Push Code to GitHub

```bash
cd /workspace/myremote-android
git init
git add .
git commit -m "Initial commit: MyRemote Admin with GitHub Actions"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

### Step 2: Enable GitHub Actions

1. Go to your GitHub repository
2. Click the **Actions** tab
3. If prompted, click **I understand my workflows, go ahead and enable them**

### Step 3: Watch the Build

- The workflow will automatically start on push
- Takes ~10-15 minutes for first build (downloads Android SDK)
- Subsequent builds are faster with caching

### Step 4: Download Your APK

1. Click on the completed workflow run
2. Scroll to **Artifacts** section at bottom
3. Click `myremote-admin-debug-apk` or `myremote-admin-release-unsigned-apk`
4. Install on your Android device

## 🔐 Optional: Configure Signed Releases

For production-ready APKs, configure these GitHub secrets:

### Generate Keystore (One Time)

```bash
keytool -genkey -v \
  -keystore myremote-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias myremote
```

### Add GitHub Secrets

Go to: **Settings → Secrets and variables → Actions → New repository secret**

| Secret Name | Value |
|-------------|-------|
| `GOOGLE_SERVICES_JSON` | Contents of your Firebase `google-services.json` |
| `RELEASE_KEYSTORE_BASE64` | Run: `base64 -w0 myremote-release.jks` |
| `KEYSTORE_PASSWORD` | Your keystore password |
| `KEY_ALIAS` | Your key alias (e.g., `myremote`) |
| `KEY_PASSWORD` | Your key password |

### Build Signed APK

1. Go to **Actions** tab
2. Select **Build Signed Release APK** workflow
3. Click **Run workflow**
4. Enter version name (e.g., `1.0.0`)
5. Click **Run workflow**
6. Download signed APK from artifacts

## 📋 Workflow Details

### Automatic Build Workflow (`build.yml`)

**Triggers:**
- Push to `main` or `master` branch
- Pull requests
- Manual trigger from Actions tab

**Outputs:**
- `myremote-admin-debug.apk` - For testing (30 days retention)
- `myremote-admin-release-unsigned.apk` - For signing (30 days retention)

**Process:**
1. Checkout code
2. Setup JDK 17
3. Setup Android SDK
4. Create placeholder `google-services.json`
5. Build debug APK
6. Upload debug artifact
7. Build release APK
8. Upload release artifact

### Signed Build Workflow (`build-signed.yml`)

**Triggers:**
- Manual only (workflow_dispatch)

**Inputs:**
- Version name (required, default: `1.0.0`)

**Outputs:**
- `myremote-admin-release-signed-{version}.apk` (90 days retention)

**Requirements:**
- All 5 GitHub secrets must be configured

## 🔧 Customization Options

### Change Build Triggers

Edit `.github/workflows/build.yml`:

```yaml
on:
  push:
    branches: [ main, develop ]  # Add more branches
  schedule:
    - cron: '0 2 * * *'  # Nightly builds at 2 AM
```

### Change Artifact Retention

```yaml
uses: actions/upload-artifact@v4
with:
  retention-days: 90  # Keep artifacts longer
```

### Add Testing

```yaml
- name: Run Tests
  run: ./gradlew test
  working-directory: ./myremote-android
```

## 📱 Installing the APK

### On Physical Device

1. Download APK to device
2. Enable **Unknown Sources** or **Install unknown apps**
3. Open APK file and install
4. Launch **MyRemote Admin**

### Via ADB

```bash
adb install app-debug.apk
```

## ⚠️ Important Notes

### Firebase Configuration

The workflow creates a placeholder `google-services.json`. For the app to work:

1. **Option A**: Add `GOOGLE_SERVICES_JSON` secret (recommended)
2. **Option B**: Manually replace after downloading APK
3. **Option C**: Build locally with real config

### Security Considerations

- Never commit `google-services.json` to public repos
- Keep keystore and passwords secure
- Signed APKs are for trusted distribution only
- App requires extensive permissions - use responsibly

### Build Times

- First build: 10-15 minutes (SDK download)
- Subsequent builds: 3-5 minutes (with caching)
- Signed builds: Same as unsigned + signing time

## 🐛 Troubleshooting

### Workflow Doesn't Start

- Check if Actions are enabled in repo settings
- Verify workflow files are in correct path: `.github/workflows/`
- Check for YAML syntax errors

### Build Fails

- View workflow logs for specific error
- Common issues:
  - Missing `google-services.json` (use secret or placeholder)
  - Gradle sync issues (check dependencies)
  - Memory issues (workflow has sufficient RAM by default)

### APK Won't Install

- Enable **Unknown Sources** on device
- Check Android version compatibility (min API 26)
- Verify APK downloaded completely
- Try debug APK first (no signature issues)

### Signed Build Fails

- Verify all 5 secrets are set correctly
- Check base64 encoding: `base64 -w0 keystore.jks | base64 -d | keytool -list`
- Ensure passwords match exactly
- Check key alias is correct

## 📊 Next Steps

1. ✅ Push code to GitHub
2. ✅ Enable Actions
3. ✅ Run first automatic build
4. ⏳ Download and test debug APK
5. ⏳ Configure Firebase project
6. ⏳ Set up secrets for signed builds
7. ⏳ Deploy to devices

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android CI/CD Best Practices](https://developer.android.com/studio/build/building-cmdline)
- [Firebase Setup Guide](https://firebase.google.com/docs/android/setup)

---

**Need Help?** Check the workflow logs in GitHub Actions for detailed build information.
