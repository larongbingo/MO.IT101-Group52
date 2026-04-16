# MotorPH
School project to MMDC

## Run
- Console App from MOIT101
  - `./amper run --module motorph-payroll-console --main-class org.motorph.Main`
- Desktop App
  - `./amper run --module jvm-app --main-class MainKt`

## Project Structure
- `/motorph-payroll-console`
  - Console App
- `/motorph-payroll-core`
  - Business Logic and Domain Models
- `/motorph-payroll-app`
  - Infrastructure and UI Code for Desktop and Android App
  - `/jvm-app`
    - Desktop App
  - `/android-app`
    - Android App
  - `/shared`
    - Shared Code for `jvm-app` and `android-app