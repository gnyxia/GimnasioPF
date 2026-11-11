# Gimnasio

Aplicación web para controlar las membresías, los pagos y la asistencia de un gimnasio pequeño. Permite saber qué membresías están vencidas **antes** de que alguien entre a entrenar y en qué días y horas se llena más el gimnasio.

Hecha con **Java 21**, **Spring Boot** y **Vaadin 25** (solo componentes gratuitos). Los datos se guardan en archivos de texto `.txt`, sin base de datos.

---

## Capturas de pantalla

![Vista de miembros](<img/Screenshot 2026-11-11 at 2.22.37 PM.png>)

![Gestión de pagos](<img/Screenshot 2026-11-11 at 2.22.42 PM.png>)

![Control de portería](<img/Screenshot 2026-11-11 at 2.22.46 PM.png>)

![Vencimientos](<img/Screenshot 2026-11-11 at 2.22.47 PM.png>)

![Ficha del miembro](<img/Screenshot 2026-11-11 at 2.22.49 PM.png>)

![Estadísticas](<img/Screenshot 2026-11-11 at 2.22.50 PM.png>)

![Mapa de calor](<img/Screenshot 2026-11-11 at 2.22.55 PM.png>)

---

## Funcionalidades

| Vista | Ruta | Qué hace |
|---|---|---|
| **Miembros** | `/` | CRUD completo: crear, ver ficha, editar y eliminar (con confirmación). Búsqueda en tiempo real y filtros por estado y plan |
| **Pagos** | `/pagos` | Registra pagos que renuevan la membresía y muestra el nuevo vencimiento |
| **Portería** | `/porteria` | Entrada y salida por documento. Con la membresía vencida no se deja entrar |
| **Vencimientos** | `/vencimientos` | Membresías que vencen en los próximos 5 días, más una notificación al abrir la app |
| **Ficha** | `/ficha/{documento}` | Rutina, registro de peso por fecha e historial de asistencia de cada miembro |
| **Estadísticas** | `/estadisticas` | Asistencia por día de la semana y miembros activos frente a vencidos |
| **Mapa de calor** | `/mapa-calor` | Bonificación: cuadrícula día × hora de las últimas 4 semanas, con leyenda y detalle al hacer clic |

### Componentes investigados

- **Badge:** estado de la membresía (Activa, Por vencer, Vencida) con color verde, amarillo o rojo.
- **ProgressBar:** cuánto le queda a la membresía de cada miembro.
- **Avatar:** iniciales de cada miembro en el Grid y en su ficha.

### Funciona en el celular

El menú se colapsa en un drawer, los formularios pasan a una columna, los botones de Portería son grandes y el mapa de calor se desplaza horizontalmente.

---

## Requisitos

- **Java 21** o superior (JDK).
- **Git** (solo para clonar el repositorio).
- Conexión a internet la primera vez (Maven descarga las dependencias).

No necesitas instalar Maven: el proyecto incluye el *Maven Wrapper* (`mvnw`).

Comprueba tu versión de Java:

```
java -version
```

Debe mostrar la versión 21 o mayor. Si no, descarga un JDK desde <https://adoptium.net>.

---

## Cómo ejecutar

### 1. Clonar el repositorio

```
git clone https://github.com/gnyxia/GimnasioPF.git
cd GimnasioPF
```

### 2. Arrancar la aplicación

#### Linux y macOS

```bash
chmod +x mvnw        # solo la primera vez, si da "permission denied"
./mvnw spring-boot:run
```

#### Windows (PowerShell)

```powershell
.\mvnw spring-boot:run
```

#### Windows (Símbolo del sistema / CMD)

```bat
mvnw spring-boot:run
```

Espera a ver en la consola un mensaje como `Started GimnasioApplication`. La primera vez tarda más porque descarga dependencias y prepara el frontend.

### 3. Abrir la aplicación

Abre <http://localhost:8080> en el navegador.

### 4. Detener la aplicación

Pulsa `Ctrl + C` en la terminal. Si en Windows pregunta `Terminate batch job (Y/N)?`, responde `y`.

---

## Probar en el celular

El computador y el celular deben estar en la **misma red Wi‑Fi**.

1. Averigua la IP del computador:

   | Sistema | Comando |
   |---|---|
   | macOS | `ipconfig getifaddr en0` |
   | Linux | `hostname -I` |
   | Windows | `ipconfig` (busca "Dirección IPv4") |

2. En el navegador del celular abre `http://IP-DEL-COMPUTADOR:8080` (por ejemplo `http://192.168.1.15:8080`).

Si no carga, revisa que el firewall del computador permita conexiones al puerto 8080.

También puedes simular un celular desde el computador: en Chrome abre las herramientas de desarrollador (`F12`) y activa el modo dispositivo móvil.

---

## Pruebas automáticas

Comprueban la renovación de membresías (vigente y vencida) y el cálculo del estado.

| Sistema | Comando |
|---|---|
| Linux / macOS | `./mvnw test` |
| Windows PowerShell | `.\mvnw test` |
| Windows CMD | `mvnw test` |

---

## Autor

Daniel Mathias Ortiz Lopez

---

## Datos

Los datos se guardan en la carpeta `datos/` (en la raíz del proyecto), un archivo por tipo, con los campos separados por `|` y codificación UTF-8.

| Archivo | Formato |
|---|---|
| `miembros.txt` | `documento\|nombre\|telefono\|plan\|fechaInicio\|fechaVencimiento` |
| `pagos.txt` | `documento\|fecha\|monto\|plan` |
| `asistencias.txt` | `documento\|entrada\|salida` (salida vacía = sigue dentro) |
| `rutinas.txt` | `documento\|texto` (los saltos de línea se guardan como `¬`) |
| `pesos.txt` | `documento\|fecha\|peso` |

Ejemplo de línea de `miembros.txt`:

```
1001|Ana Pérez|3001234567|TRIMESTRAL|2026-10-31|2027-01-29
```

- **Primera ejecución:** si no existe `datos/miembros.txt`, la aplicación crea la carpeta con datos de ejemplo (8 miembros vigentes, por vencer y vencidos, con pagos y asistencias de las últimas 4 semanas).
- **Persistencia:** los datos se reescriben en cada cambio y sobreviven a un reinicio.
- **Errores de lectura:** un archivo que falta se trata como vacío y una línea mal escrita se ignora con un aviso en la consola, sin tumbar la aplicación. Si hubo líneas dañadas, se guarda una copia `archivo.txt.bak`.
- **Reiniciar los datos de ejemplo:** detén la app, borra la carpeta `datos/` y vuelve a arrancar.

> Edita los `.txt` solo con la aplicación detenida; si no, tus cambios se sobrescriben.

---

## Reglas de negocio

- **Vencimiento:** `fecha de inicio + meses del plan` (mensual = 1, trimestral = 3, anual = 12). Se calcula solo.
- **Renovación por pago:**
  - Si la membresía sigue vigente, el tiempo del plan se **suma al vencimiento actual**.
  - Si ya venció, se cuenta **desde hoy**.
- **Estado de la membresía:**
  - **Activa:** faltan más de 5 días.
  - **Por vencer:** faltan 5 días o menos (incluido el día de vencimiento).
  - **Vencida:** la fecha de vencimiento ya pasó.
- **Entrada y salida:** se buscan por documento.
  - Si el documento no existe, se avisa.
  - Con la membresía **vencida no se registra la entrada** y se muestra "acceso denegado".
  - Si ya hay una entrada sin salida, el siguiente registro es la salida (la salida siempre se permite).
- **Por vencer:** la vista de Vencimientos lista los miembros que vencen entre hoy y los próximos 5 días.
- **Eliminar un miembro** borra también sus pagos, asistencias, rutina y pesos, y siempre pide confirmación.

### Validaciones

| Campo | Regla |
|---|---|
| Documento | Obligatorio, de 5 a 12 dígitos y único |
| Nombre | Obligatorio, de 3 a 60 caracteres, solo letras y espacios |
| Teléfono | Obligatorio, 10 dígitos y empieza por 3 |
| Fecha de inicio | Entre hace un año y dentro de 30 días |
| Monto | Mayor que 0 y hasta 5.000.000 |
| Peso | Entre 20 y 300 kg, con fecha no futura |
| Rutina | Hasta 1000 caracteres |

Los errores se muestran en una `Notification` con un mensaje claro.

---

## Estructura del proyecto

```
src/main/java/com/example/gimnasio/
├── GimnasioApplication.java
├── datos/                 Clases de datos y lectura/escritura de archivos
│   ├── Plan.java          Enum: MENSUAL, TRIMESTRAL, ANUAL
│   ├── Estado.java        Enum: ACTIVA, POR_VENCER, VENCIDA
│   ├── Miembro.java
│   ├── Pago.java
│   ├── Asistencia.java
│   ├── Rutina.java
│   ├── RegistroPeso.java
│   ├── Validador.java     Reglas de validación de formularios
│   └── Almacen.java       Lee y escribe los .txt
└── vistas/                Interfaz con Vaadin
    ├── MainLayout.java    AppLayout con el menú lateral
    ├── Componentes.java   Avatar, Badge, ProgressBar y diálogo de confirmación
    ├── MiembrosView.java
    ├── PagosView.java
    ├── PorteriaView.java
    ├── VencimientosView.java
    ├── FichaView.java
    ├── EstadisticasView.java
    └── MapaCalorView.java

src/test/java/com/example/gimnasio/datos/
└── MiembroTest.java
```

---

## Solución de problemas

| Problema | Solución |
|---|---|
| `permission denied: ./mvnw` (Linux/macOS) | Ejecuta `chmod +x mvnw` |
| `JAVA_HOME not found` o versión de Java incorrecta | Instala un JDK 21 o superior y comprueba con `java -version` |
| El puerto 8080 está ocupado | Agrega `server.port=8081` en `src/main/resources/application.properties` y abre `http://localhost:8081` |
| La primera ejecución es lenta | Es normal: descarga dependencias y prepara el frontend de Vaadin |
| Los datos de ejemplo desaparecieron | Detén la app, borra la carpeta `datos/` y arranca de nuevo |
| Aparece "Línea ignorada" en la consola | Una línea de un `.txt` tiene un formato incorrecto. Revisa la tabla de formatos; la copia `.bak` conserva el original |


## Tecnologías

- Java 21
- Spring Boot
- Vaadin 25 (componentes gratuitos: Grid, Dialog, Notification, Badge, ProgressBar, Avatar, TabSheet, AppLayout, SideNav, entre otros)
- Maven (con Maven Wrapper)
- JUnit 5
