# Biblioteca Java: Primeras Aventuras en la Programación
Sir Alex.
Hay un par de aclaraciones que hacer:
Añadí atributos a las clases Book y User, pues los consideré necesarios 



















## Descripción

Bienvenidos a **Biblioteca Java: Primeras aventuras en la Programación**.
En esta práctica, desarrollarás una aplicación en Java para gestionar una biblioteca. Implementarás clases y métodos para manejar libros y usuarios, y utilizarás JUnit para verificar el correcto funcionamiento del código. Además, se incluyen desafíos adicionales para los estudiantes más avanzados.

## Instrucciones

### 1. Hacer Fork del Repositorio

1. Ve al repositorio original en GitHub.
2. Haz clic en el botón "Fork" en la esquina superior derecha para crear una copia del repositorio en tu cuenta de GitHub.
3. Clona el repositorio forkeado a tu máquina local:
   ```bash
   git clone https://github.com/<usuario>/libraryManagement.git
   ```
### 2. Instalación
Abre el proyecto en tu IDE (IntelliJ, Eclipse, Visual Studio Code).
Asegúrate de tener instalado Java y Maven.

### 3. Ejecución

1.  Ejecuta la clase Biblioteca para probar la funcionalidad básica.
2.  Ejecuta los tests de JUnit en la clase BibliotecaTest para verificar el correcto funcionamiento.

### 4.  Ejercicios a Realizar

#### 1. Clase Book
      En la clase Book los atributos son: title (String), author (String), isbn (String), available (boolean)
      Implementa un constructor para inicializar los atributos.
      Implementa métodos getters y setters para los atributos.
      Implementa un método toString para mostrar la información del libro.

#### 2. Clase User
      Implementa un método borrowBook para añadir un libro al lista de libros prestados.
      Implementa un método returnBook para eliminar un libro del lista de libros prestados.

#### 3. Clase Biblioteca
      Crea una clase Biblioteca con los siguientes atributos: libros (Lista de Libro), usuarios (Lista de Usuario).
      Implementa un constructor para inicializar los atributos.
      Implementa métodos para añadir libros y usuarios.
      Implementa métodos para prestar y devolver libros.
      Implementa métodos para buscar libros por título y autor.

### 5. Desafíos Adicionales

#### 1. Implementar búsqueda de libros:

      Añade un método en la clase Biblioteca para buscar libros por título o autor.
      Añade un método en la clase Biblioteca para buscar libros que esten disponibles.

#### 2. Desafíos para los más avanzados:

      Implementa un método reserveBook en la clase User para reservar libros que no están disponibles.
##### Extra extra:
      En la rama del repositorio de github denomidada bbdd, está implementada una conexión a una base de datos 
      utilizando JDBC para almacenar y recuperar información de libros y usuarios. Haz uso de la clase LibraryJDBC, y
      de las clases DAO, UserDAO y BookDAO
      Todas las funcionalidades de usuario están funcionando:
      Faltan las sentencias SQL de la clase Libros, BookDAO, para que funcione totalmente la biblioteca

### 6. Contacto
Si tienes dudas, [nos puedes enviar un email](ing-tomillo-java@ing.com)