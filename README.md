# Atiende

## ¿Qué es el proyecto?

Atiende es un backend de atención al cliente para Grano Alto Café, pensado para centralizar mensajes recibidos por WhatsApp, correo y chat web en tickets con estado y prioridad. La idea es tener un registro único para saber qué mensajes faltan por atender y en qué estado se encuentran.

La aplicación sigue la arquitectura del proyecto:
- `com.devsenior.atiende.ticket` para el dominio de tickets
- capas `Controller -> Service -> Repository`
- DTOs como `record` en `ticket/dto`
- mapper propio por dominio
- manejo de errores con excepciones propias y `@RestControllerAdvice`

## Requisitos

- Java 25
- Maven (o Maven Wrapper incluido en el proyecto)
- MySQL local
- Base de datos disponible en `localhost:3306` con el nombre `atiende_db`
- Configuración de la base de datos en `src/main/resources/application.properties`

## Cómo ejecutarlo

1. Asegúrate de que MySQL esté levantado y accesible.
2. Verifica la configuración en `src/main/resources/application.properties`:
   - `spring.datasource.url=jdbc:mysql://localhost:3306/atiende_db?createDatabaseIfNotExist=true`
   - `spring.datasource.username=root`
   - `server.port=8081`
3. Desde la raíz del proyecto, ejecuta:

   En Windows:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

   En macOS/Linux:
   ```bash
   ./mvnw spring-boot:run
   ```

4. La API quedará disponible en:
   ```text
   http://localhost:8081
   ```

## Endpoints

Base: `/api/tickets`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| POST | `/api/tickets` | Crea un ticket | 201 · 400 |
| GET | `/api/tickets` | Lista tickets, más recientes primero; el filtro es opcional | 200 |
| GET | `/api/tickets/{id}` | Busca un ticket por id | 200 · 404 |
| PATCH | `/api/tickets/{id}/estado` | Cambia el estado del ticket | 200 · 400 · 404 · 409 |

### Filtro opcional en listado

`GET /api/tickets?estado=ABIERTO`

## Ejemplo curl

### Crear un ticket

```bash
curl -X POST http://localhost:8081/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "clienteNombre": "Ana",
    "clienteContacto": "ana@correo.com",
    "canal": "WHATSAPP",
    "mensaje": "Necesito ayuda con mi pedido"
  }'
```

### Cambiar estado de un ticket

```bash
curl -X PATCH http://localhost:8081/api/tickets/1/estado \
  -H "Content-Type: application/json" \
  -d '{
    "estado": "EN_PROCESO"
  }'
```

## Tabla de transiciones

### Estados

- `ABIERTO`
- `EN_PROCESO`
- `RESUELTO`
- `CERRADO`

### Transiciones permitidas

| Desde | Hacia | Caso real |
|---|---|---|
| `ABIERTO` | `EN_PROCESO` | Alguien de soporte toma el ticket |
| `ABIERTO` | `CERRADO` | Spam o mensaje duplicado |
| `EN_PROCESO` | `RESUELTO` | Se respondió al cliente |
| `RESUELTO` | `CERRADO` | El cliente confirmó la solución |
| `RESUELTO` | `EN_PROCESO` | El cliente dice que no quedó resuelto |

Cualquier otra transición se rechaza. `CERRADO` es un estado final.

## Formato de error

La API responde con JSON con la siguiente estructura:

```json
{
  "codigo": "ERROR",
  "error": "RecursoNoEncontradoException",
  "mensaje": "No existe un ticket con id 999",
  "fecha": "2026-10-21T11:42:24"
}
```

Los campos esperados son:
- `codigo`
- `error`
- `mensaje`
- `fecha`

## Reglas de negocio principales

1. Todo ticket nuevo nace en `ABIERTO` y con prioridad `MEDIA`.
2. El cliente no puede enviar `estado` ni `prioridad` al crear el ticket.
3. La regla de transiciones vive en el enum `EstadoTicket`, en el método `puedeCambiarA(EstadoTicket destino)`.
4. Cualquier transición no permitida devuelve error de negocio.

## Cómo correr los tests

Desde la raíz del proyecto:

```bash
./mvnw test
```

También se pueden ejecutar tests específicos, por ejemplo:

```bash
./mvnw -Dtest=EstadoTicketTest,TicketServiceTest test
```

Los tests están en `src/test/java` y usan JUnit 5 + Mockito sin levantar Spring.
