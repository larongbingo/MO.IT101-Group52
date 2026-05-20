# MotorPH
School project to MMDC

## Run
- Swing Desktop App from MOIT103
  - `./amper run --module motorph-payroll-swing-ui --main-class org.motorph.Main`
  - Credentials (Username/Password):
    - staff1/staff1 - CEO
    - staff11/staff11 - Payroll Team Lead

## Project Structure
- `/motorph-payroll-core` - Business Logic and Domain Models
- `/motorph-payroll-swing-ui` - Infrastructure, Swing UI and JVM Runtime
- `/amper-swing-plugin` - Amper plugin for Swing Form UI, generates the UI code from form files
- `/amper-swing-runtime` - Amper runtime for Swing Form UI, contains a helper class to initialize the UI

### Note on Amper
Note that the developer used Amper to check and experiment with the build tool.
On first run, it may take a while to download the dependencies.

If you encounter any issues, please refer to the Amper documentation for troubleshooting.

Additionally, Amper doesn't have support for Swing Form UI (.form files). To fix this, we generated an Amper Plugin
to generate the UI form into Kotlin. 

### Note on Swing Form UI
- The generated classes are postfixed with `FormHelper` example `LoginForm` -> `LoginFormFormHelper`
- Call the `SwingForms.init(this)` to initialize the form