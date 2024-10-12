
# REST API para Sistema de Gestión de Bares

## Descripción del Proyecto

Este proyecto es un sistema de gestión para bares, diseñado para facilitar la administración de mesas, bebidas y ventas. Permite a los dueños y empleados registrar mesas, gestionar el inventario de bebidas, y llevar un control detallado de las ventas realizadas en el establecimiento.

## Contexto

El sistema está diseñado para que el dueño de un bar pueda:

1. **Registrar Espacios de Mesas**: Definir áreas como "Entrada Principal" y "Terraza", especificando la cantidad de mesas en cada área.
2. **Gestionar el Inventario de Bebidas**: Registrar bebidas con su nombre, descripción, precio, categoría e imagen.
3. **Realizar Ventas**: Facilitar la atención al cliente mediante la gestión de mesas y pedidos.

## Estructura del Proyecto

El proyecto sigue el patrón arquitectónico Model-View-Controller (MVC) y está organizado en las siguientes carpetas:

```
businessmanagement/
├── application/
│   ├── controllers/
│   ├── dtos/
│   └── services/
├── domain/
│   ├── entities/
│   └── enums/
└── infrastructure/
    └── repositories/
```

### Componentes Principales

- **BeverageManagement**: Manejo del inventario de bebidas.
- **BusinessManagement**: Gestión de los espacios y mesas del bar.
- **SalesHistoryManagement**: Registro y consulta del historial de ventas.
- **SalesManagement**: Manejo de las ventas actuales.
- **SecurityManagement**: Autenticación y autorización mediante JWT.
- **TableManagement**: Administración de mesas y su estado.

## Funcionalidades Clave

### Registro de Mesas
Los dueños pueden registrar espacios como "Entrada Principal - 5 mesas" y "Terraza - 10 mesas".

### Nueva Venta
Al iniciar una nueva venta, se muestran los espacios registrados con sus respectivas mesas disponibles:

- Mesas libres se indican con un fondo verde.
- Cada mesa tiene un botón "Atender" que permite gestionar pedidos específicos.

### Atención a Mesas
Al seleccionar "Atender", se muestra:

1. El nombre del espacio y número de mesa.
2. Un botón para regresar a la vista anterior.
3. Un botón "Agregar Bebidas" que abre un modal con una tabla paginada para seleccionar bebidas.

### Tabla de Pedidos
Al agregar bebidas, se muestra una tabla con:

- Nombre de la bebida
- Cantidad (editable)
- Precio por unidad
- Subtotal
- Estado de entrega (con opción para marcar como entregado)
- Opción para quitar un pedido

### Total y Cobro
Se calcula el total del pedido y se proporciona un botón "Cobrar" que confirma la transacción.

### Almacenamiento
Todos los datos son almacenados en una base de datos MySQL, incluyendo imágenes guardadas en Firebase Cloud Storage.

## Tecnologías Utilizadas

- **Java**: Lenguaje principal del backend.
- **Spring Boot**: Framework utilizado para construir el backend.
- **JWT**: Para autenticación y autorización segura.
- **MySQL**: Base de datos relacional.
- **Firebase Cloud Storage**: Para almacenamiento de imágenes.

## Instalación

1. Clona este repositorio:
   ```bash
   git clone <URL-del-repositorio>
   ```

2. Navega al directorio del proyecto:
   ```bash
   cd businessmanagement
   ```

3. Configura tu base de datos MySQL y actualiza las credenciales en `application.properties`.

4. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

## Contribuciones

Las contribuciones son bienvenidas. Si deseas contribuir, por favor sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m 'Añadir nueva funcionalidad'`).
4. Envía un pull request.
