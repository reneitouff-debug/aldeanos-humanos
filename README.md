# Aldeanos Humanos (Fabric 1.21.1)

Cada aldeano recibe un nombre, una personalidad (Amable, Gruñón, Tímido, Alegre) y recuerda
su nivel de amistad con cada jugador.

## Cómo interactuar
- **Clic derecho**: comercio normal.
- **Shift + clic derecho con la mano vacía**: conversar. Los diálogos dependen de personalidad,
  profesión, hora del día, lluvia y amistad. +2 de cariño una vez al día.
- **Shift + clic derecho con un regalo**: pan, manzana, galleta, flores, zanahoria dorada, pastel,
  esmeralda, diamante... suben la amistad y generan descuentos reales de comercio.
- **Golpear a un aldeano**: -15 de amistad (si llega a negativo, deja de hablarte).

## Compilar
Requiere JDK 21.
    ./gradlew build     (Windows: gradlew.bat build)
El .jar queda en build/libs/. Ponlo en .minecraft/mods junto a Fabric Loader y Fabric API.

## Compilar sin instalar nada: GitHub Actions
1. Crea un repositorio vacío en github.com y sube todo el contenido de esta carpeta
   (incluida la carpeta oculta .github).
2. Ve a la pestaña Actions, abre la ejecución "Compilar mod" y espera ~3 minutos.
3. Al terminar, descarga el artefacto "aldeanos-humanos-mod" (un zip con el .jar dentro).
