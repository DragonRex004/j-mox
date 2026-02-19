# 🚀 J-Mox (Java Hypervisor Manager)

J-Mox ist ein leichtgewichtiger, webbasierter Hypervisor-Manager, der in Java geschrieben wurde. Er ermöglicht die zentrale Steuerung von VirtualBox-Instanzen über ein modernes Dashboard im "Purple Night"-Design.



## ✨ Features

* **Live Monitoring:** Host-Auslastung (CPU & RAM) in Echtzeit via OSHI.
* **Hypervisor Control:** Starten/Stoppen von VMs direkt aus dem Browser.
* **Sicherer Zugriff:** Authentifizierung mittels BCrypt-Hashing und Session-Management.
* **User Management:** Integrierte Benutzerverwaltung mit SQLite-Datenbank.
* **Modern UI:** Responsives Design mit Tailwind CSS (optimiert für Desktop & Mobile).
* **Admin CLI:** Interaktive JLine3-Konsole im Terminal zur Server-Steuerung.

## 🛠 Tech Stack

| Komponente | Technologie |
| :--- | :--- |
| **Backend** | Java 17+, Javalin Framework |
| **Frontend** | HTML5, Tailwind CSS, JavaScript (Vanilla) |
| **Datenbank** | SQLite (User-Daten & Settings) |
| **Sicherheit** | jBCrypt (Password Hashing) |
| **Monitoring** | OSHI (Operating System and Hardware Information) |
| **CLI** | JLine3 |

## 🚀 Schnellstart

### Voraussetzungen
* **JDK 17** oder höher installiert.
* **VirtualBox** installiert und im System-Pfad (für `VBoxManage`).
* **Maven** zum Bauen des Projekts.

### Installation
1. Repository klonen:
```bash
   git clone https://github.com/DragonRex004/j-mox.git
   cd j-mox
```
Projekt bauen:

```bash
  mvn clean install
```
Applikation starten:

```bash
java -jar target/j-mox-1.0-SNAPSHOT.jar
```
Standard-Zugang
Nach dem ersten Start wird automatisch ein Admin-Konto erstellt:

URL: http://localhost:8080

User: admin

Password: admin (Sollte sofort über die Konsole oder das Dashboard geändert werden!)

### 🖥 Screenshot-Vibe
Dashboard: Dunkles Interface mit lila Akzenten, Fortschrittsbalken für Hardware-Ressourcen und eine saubere Tabelle für die VM-Steuerung.

### 🛤 Roadmap
[x] SQLite User Management

[x] BCrypt Login-System

[ ] ISO-Datei Upload & Management

[ ] Support für KVM/Libvirt (Linux-Backend)

[ ] VM Snapshot Management

Made with 💜 and Java.


---