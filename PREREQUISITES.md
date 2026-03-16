# Prerequisites Setup Guide (Windows)

Step-by-step instructions to install **Maven** and **Docker Desktop** and add them to your PATH.

---

## 1. Install Maven

### Step 1.1 – Download Maven

1. Open: **https://maven.apache.org/download.cgi**
2. Under **Files**, click the **Binary zip archive** link (e.g. `apache-maven-3.9.6-bin.zip`).
3. Save the ZIP file (e.g. to your `Downloads` folder).

### Step 1.2 – Extract Maven

1. Right-click the downloaded ZIP → **Extract All**.
2. Choose a permanent location, for example:
   - `C:\Program Files\Apache\maven`
   - or `C:\Tools\apache-maven-3.9.6`
3. Extract. You should see a folder like `apache-maven-3.9.6` with subfolders `bin`, `lib`, `conf`, etc.

**Note:** Remember this folder path; you’ll add the **bin** folder to PATH (e.g. `C:\Tools\apache-maven-3.9.6\bin`).

### Step 1.3 – Add Maven to system PATH

1. Press **Win + R**, type `sysdm.cpl`, press **Enter** (opens System Properties).
2. Go to the **Advanced** tab → click **Environment Variables**.
3. Under **System variables** (or **User variables** if you prefer only for your account), select **Path** → click **Edit**.
4. Click **New** and add the **bin** folder path, for example:
   ```
   C:\Tools\apache-maven-3.9.6\bin
   ```
   (Use your actual path; it must end with `\bin`.)
5. Click **OK** on all dialogs to save.

### Step 1.4 – Verify Maven

1. **Close and reopen** PowerShell (or open a new terminal).
2. Run:
   ```powershell
   mvn -version
   ```
   You should see Maven version and Java version. If you get “not recognized”, the PATH is wrong or the terminal wasn’t restarted.

---

## 2. Install Docker Desktop

### Step 2.1 – Download Docker Desktop

1. Open: **https://www.docker.com/products/docker-desktop**
2. Click **Download for Windows**.
3. Run the installer when the download finishes.

### Step 2.2 – Run the installer

1. If asked, ensure **Use WSL 2 instead of Hyper-V** is selected (recommended on Windows 10/11).
2. If the installer says “WSL 2 installation is incomplete”, it will open a page with a link to install the WSL 2 kernel. Download and run that, then restart if required and run the Docker installer again.
3. Complete the installer (accept license, optional “Add shortcut to desktop”).
4. Choose **Restart** when prompted, or restart manually so Docker can start correctly.

### Step 2.3 – Start Docker Desktop

1. After restart, start **Docker Desktop** from the Start menu.
2. Wait until the whale icon in the system tray is steady (not animating). This can take a minute the first time.
3. If you see a “Docker Desktop starting…” message, wait until it says Docker is running.

### Step 2.4 – Verify Docker

1. Open a **new** PowerShell window (so it picks up the updated PATH).
2. Run:
   ```powershell
   docker --version
   docker compose version
   ```
   You should see version numbers. If “docker” is not recognized, restart PowerShell again or log out and back in.

---

## 3. Quick checklist

Before running the app:

- [ ] Maven installed and **bin** folder added to PATH.
- [ ] `mvn -version` works in a **new** PowerShell.
- [ ] Docker Desktop installed and **restart** done if the installer asked for it.
- [ ] Docker Desktop is **running** (whale icon in tray).
- [ ] `docker --version` and `docker compose version` work in a **new** PowerShell.

Then you can run from the project root:

```powershell
.\start.ps1
```

and follow the two-terminal steps in **SETUP_GUIDE.md** to start the backend and frontend.
