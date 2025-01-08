// CÓDIGO DE MI SERVIDOR ALOJADO EN GLITCH.  LENGUAJE: NODE.JS


const fetch = require('node-fetch');
const fs = require('fs');

const express = require("express");
const admin = require("firebase-admin");
const bodyParser = require("body-parser");
require('dotenv').config(); // Cargar las variables de entorno desde el archivo .env

const app = express();
const port = process.env.PORT || 5000; // Usar el puerto proporcionado por la plataforma

app.use(bodyParser.json());

// Construir las credenciales de Firebase desde las variables de entorno
const serviceAccount = {
    type: "service_account",
    project_id: process.env.FIREBASE_PROJECT_ID,
    private_key_id: process.env.FIREBASE_PRIVATE_KEY_ID,
    private_key: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n'), 
    client_email: process.env.FIREBASE_CLIENT_EMAIL,
    client_id: process.env.FIREBASE_CLIENT_ID,
    auth_uri: process.env.FIREBASE_AUTH_URI,
    token_uri: process.env.FIREBASE_TOKEN_URI,
    auth_provider_x509_cert_url: process.env.FIREBASE_AUTH_PROVIDER_X509_CERT_URL,
    client_x509_cert_url: process.env.FIREBASE_CLIENT_X509_CERT_URL
  
};

// Inicializar Firebase Admin SDK
try {
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
    });

    console.log("Firebase Admin SDK inicializado correctamente.");
} catch (error) {
    console.error("Error al inicializar Firebase Admin SDK:", error);
    process.exit(1);
}

// Obtener instancia de Firestore
const db = admin.firestore();

// Ruta de prueba
app.get("/", (req, res) => {
    res.send("¡Servidor funcionando correctamente!");
});

app.post("/registrarUsuario", async (req, res) => {
    try {
        const { email, password, nombreUsuario } = req.body;

        // Verificar que todos los campos obligatorios estén presentes
        if (!email || !password || !nombreUsuario) {
            return res.status(400).json({ error: "Email, contraseña y nombre de usuario son obligatorios." });
        }

        // Verificar si el nombre de usuario ya existe en Firestore
        const usuariosSnapshot = await db.collection("usuarios")
            .where("nombreUsuario", "==", nombreUsuario)
            .get();

        if (!usuariosSnapshot.empty) {
            return res.status(400).json({ error: "El nombre de usuario ya está en uso." });
        }

        try {
            // Intentar crear el usuario en Firebase Authentication
            const userRecord = await admin.auth().createUser({
                email,
                password,
            });

            // Guardar datos adicionales en Firestore
            await db.collection("usuarios").doc(userRecord.uid).set({
                email,
                nombreUsuario,
                comodinesUsados: 0,
                partidasJugadas: 0,
                partidasGanadas: 0,
                preguntasAcertadas: 0,
                preguntasRespondidas: 0,
                currentRacha: 0,
                maxRacha: 0,
            });

            // Respuesta exitosa
            res.status(201).json({ message: "Usuario registrado con éxito." });
        } catch (authError) {
            // Manejo de errores específicos de Firebase Authentication
            if (authError.code === "auth/email-already-exists") {
                return res.status(400).json({ error: "El correo electrónico ya está en uso." });
            } else if (authError.code === "auth/invalid-password") {
                return res.status(400).json({ error: "La contraseña debe tener al menos 6 caracteres." });
            } else {
                // Si es otro error, lo lanza para que sea manejado más arriba
                throw authError;
            }
        }
    } catch (error) {
        console.error("Error al registrar el usuario:", error);
        res.status(500).json({ error: "Error interno al registrar el usuario." });
    }
});





// Obtener preguntas aleatorias por nivel
app.post("/obtenerPreguntas", async (req, res) => {
    const { nivel, cantidad } = req.body;

    if (!nivel || !cantidad) {
        return res.status(400).send({ error: "Nivel y cantidad son obligatorios." });
    }

    try {
        const db = admin.firestore();
        const preguntasRef = db.collection("preguntas");
        const snapshot = await preguntasRef.get();

        // Filtrar preguntas por nivel
        const preguntasNivel = snapshot.docs.filter(doc => doc.get("dificultad") === nivel);

        if (preguntasNivel.length < cantidad) {
            return res.status(400).send({ error: "No hay suficientes preguntas para el nivel especificado." });
        }

        // Seleccionar preguntas aleatorias
        const preguntasAleatorias = [];
        const indicesUsados = new Set();

        while (preguntasAleatorias.length < cantidad) {
            const randomIndex = Math.floor(Math.random() * preguntasNivel.length);
            if (!indicesUsados.has(randomIndex)) {
                indicesUsados.add(randomIndex);
                preguntasAleatorias.push(preguntasNivel[randomIndex].data());
            }
        }

        res.status(200).send(preguntasAleatorias);
    } catch (error) {
        console.error("Error al obtener preguntas:", error);
        res.status(500).send({ error: "Error al obtener preguntas." });
    }
});
app.post("/iniciarSesion", async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).send({ error: "Email y contraseña son obligatorios." });
        }

        // Llamar a la Firebase REST API para el inicio de sesión
        const apiKey = process.env.API_KEY; // API_KEY desde las variables de entorno
        const url = `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${apiKey}`;

        const response = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                email: email,
                password: password,
                returnSecureToken: true,
            }),
        });

        if (response.ok) {
            const data = await response.json();

            // Obtener el UID del token
            const uid = data.localId;

            // Buscar el documentId en Firestore usando el UID
            const userDocRef = db.collection("usuarios").doc(uid);
            const userDoc = await userDocRef.get();

            if (userDoc.exists) {
                // Obtener los datos del usuario desde Firestore
                const userData = userDoc.data();
                
                // Asegurarse de tener valores predeterminados en caso de que algunos campos no existan
                const nombreUsuario = userData.nombreUsuario || "Usuario sin nombre";
            

                // Devolver los datos del usuario junto con el email y documentId
                res.status(200).send({
                    message: "Inicio de sesión exitoso.",
                    email: data.email,
                    documentId: userDoc.id, // El documentId de Firestore
                    nombreUsuario: nombreUsuario,
                   
                });
            } else {
                res.status(404).send({ error: "Usuario no encontrado." });
            }
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.error?.message || "Error desconocido.";
            res.status(401).send({ error: `Inicio de sesión fallido: ${errorMessage}` });
        }
    } catch (error) {
        console.error("Error en el inicio de sesión:", error);
        res.status(500).send({ error: "Error inesperado en el servidor." });
    }
});


app.post("/actualizarPartidasJugadas", async (req, res) => {
    try {
        const { documentId } = req.body;

        if (!documentId) {
            return res.status(400).send({ error: "El documentId es obligatorio." });
        }

        const userDocRef = db.collection("usuarios").doc(documentId);
        const userDoc = await userDocRef.get();

        if (!userDoc.exists) {
            return res.status(404).send({ error: "Usuario no encontrado." });
        }

        await userDocRef.update({
            partidasJugadas: admin.firestore.FieldValue.increment(1)
        });

        res.status(200).send({ message: "Partidas jugadas actualizadas correctamente." });
    } catch (error) {
        console.error("Error al actualizar partidas jugadas:", error);
        res.status(500).send({ error: "Error interno del servidor." });
    }
});

app.post("/actualizarRacha", async (req, res) => {
    try {
        const { documentId, acertada } = req.body;

        if (!documentId) {
            return res.status(400).send({ error: "El documentId es obligatorio." }); // 400: El documentId es obligatorio
        }

        // Obtener la referencia al documento en Firestore
        const userDocRef = db.collection("usuarios").doc(documentId);

        // Verificar si el documento existe
        const userDoc = await userDocRef.get();
        if (!userDoc.exists) {
            return res.status(404).send({ error: "Usuario no encontrado." }); // 404: Usuario no encontrado
        }

        // Obtener la racha actual y la mayor racha de aciertos
        const userData = userDoc.data();
        let currentRacha = userData.currentRacha || 0;
        let maxRacha = userData.maxRacha || 0;

        // Si la respuesta es correcta, incrementamos la racha
        if (acertada) {
            currentRacha++;
        } else {
            // Si la respuesta es incorrecta, restablecemos la racha actual
            currentRacha = 0;
        }

        // Verificar si la racha actual es la mayor
        if (currentRacha > maxRacha) {
            maxRacha = currentRacha;
        }

        // Actualizar el documento con la nueva racha
        await userDocRef.update({
            currentRacha: currentRacha,
            maxRacha: maxRacha
        });

        res.status(204).send(); // 204: Éxito sin contenido
    } catch (error) {
        console.error("Error al actualizar la racha:", error);
        res.status(500).send({ error: "Error interno del servidor." }); // 500: Error interno del servidor
    }
});


app.post("/actualizarProgreso", async (req, res) => {
    try {
        const { documentId, preguntasRespondidas, preguntasAcertadas } = req.body;

        if (!documentId) {
            return res.status(400).send(); // 400: El documentId es obligatorio
        }

        // Obtener la referencia al documento en Firestore
        const userDocRef = db.collection("usuarios").doc(documentId);

        // Verificar si el documento existe
        const userDoc = await userDocRef.get();
        if (!userDoc.exists) {
            return res.status(404).send(); // 404: Usuario no encontrado
        }

        // Crear objeto con las actualizaciones
        const updates = {};
        if (preguntasRespondidas !== undefined) {
            updates.preguntasRespondidas = admin.firestore.FieldValue.increment(preguntasRespondidas);
        }
        if (preguntasAcertadas !== undefined) {
            updates.preguntasAcertadas = admin.firestore.FieldValue.increment(preguntasAcertadas);
        }

        // Aplicar las actualizaciones solo si hay algo que actualizar
        if (Object.keys(updates).length > 0) {
            await userDocRef.update(updates);
        }

        res.status(204).send(); // 204: Éxito sin contenido
    } catch (error) {
        console.error("Error al actualizar progreso:", error);
        res.status(500).send(); // 500: Error interno del servidor
    }
});

app.post("/actualizarComodines", async (req, res) => {
    try {
        const { documentId } = req.body;

        if (!documentId) {
            return res.status(400).send({ error: "El documentId es obligatorio." }); // 400: El documentId es obligatorio
        }

        // Obtener la referencia al documento en Firestore
        const userDocRef = db.collection("usuarios").doc(documentId);

        // Verificar si el documento existe
        const userDoc = await userDocRef.get();
        if (!userDoc.exists) {
            return res.status(404).send({ error: "Usuario no encontrado." }); // 404: Usuario no encontrado
        }

        // Incrementar el número de comodines usados
        await userDocRef.update({
            comodinesUsados: admin.firestore.FieldValue.increment(1)
        });

        res.status(204).send(); // 204: Éxito sin contenido
    } catch (error) {
        console.error("Error al actualizar comodines:", error);
        res.status(500).send({ error: "Error interno del servidor." }); // 500: Error interno del servidor
    }
});


app.post("/incrementarPartidasGanadas", async (req, res) => {
    try {
        const { documentId } = req.body;

        if (!documentId) {
            return res.status(400).send(); // 400: El documentId es obligatorio
        }

        // Obtener la referencia al documento en Firestore
        const userDocRef = db.collection("usuarios").doc(documentId);

        // Verificar si el documento existe
        const userDoc = await userDocRef.get();
        if (!userDoc.exists) {
            return res.status(404).send(); // 404: Usuario no encontrado
        }

        // Incrementar el campo "partidasGanadas"
        await userDocRef.update({
            partidasGanadas: admin.firestore.FieldValue.increment(1)
        });

        res.status(204).send(); // 204: Éxito sin contenido
    } catch (error) {
        console.error("Error al incrementar partidas ganadas:", error);
        res.status(500).send(); // 500: Error interno del servidor
    }
});

app.post("/obtenerEstadisticasUsuario", async (req, res) => {
    try {
        const { documentId } = req.body; // Recibir el documentId desde el cuerpo de la solicitud

        if (!documentId) {
            return res.status(400).send({ error: "El documentId es obligatorio." });
        }

        // Obtener la referencia al documento en Firestore
        const userDocRef = db.collection("usuarios").doc(documentId);

        // Verificar si el documento existe
        const userDoc = await userDocRef.get();
        if (!userDoc.exists) {
            return res.status(404).send({ error: "Usuario no encontrado." });
        }

        // Obtener los datos del usuario
        const userData = userDoc.data();

        const partidasJugadas = userData.partidasJugadas || 0;
        const partidasGanadas = userData.partidasGanadas || 0;
        const preguntasAcertadas = userData.preguntasAcertadas || 0;
        const preguntasRespondidas = userData.preguntasRespondidas || 1; // Evitar división por cero
        const comodinesUsados = userData.comodinesUsados || 0;
        const maxRacha = userData.maxRacha || 0;

        // Calcular el porcentaje de aciertos
        const porcentajeAciertos = parseFloat(((preguntasAcertadas / preguntasRespondidas) * 100).toFixed(2));

        // Devolver todos los datos
        res.status(200).send({
            partidasJugadas: partidasJugadas,
            partidasGanadas: partidasGanadas,
            porcentajeAciertos: porcentajeAciertos,
            comodinesUsados: comodinesUsados,
            maxRacha: maxRacha
        });
    } catch (error) {
        console.error("Error al obtener las estadísticas del usuario:", error);
        res.status(500).send({ error: "Error inesperado en el servidor." });
    }
});

app.post('/obtenerRanking', async (req, res) => {
    try {
        // Definir la cantidad de jugadores a obtener (por ejemplo, los primeros 10)
        const cantidad = req.body.cantidad || 10;

        // Obtener los usuarios de Firestore
        const usuariosRef = db.collection("usuarios");
        const snapshot = await usuariosRef.get();

        // Si no hay usuarios, enviar un error
        if (snapshot.empty) {
            return res.status(404).send({ error: "No hay usuarios registrados." });
        }

        // Crear un arreglo de usuarios con sus partidas jugadas y ganadas
        const ranking = snapshot.docs.map(doc => {
            const data = doc.data();
            return {
                id: doc.id,
                nombreUsuario: data.nombreUsuario || data.email.split('@')[0],  // Usar nombreUsuario si existe, sino, usar la parte antes del '@'
                partidasJugadas: data.partidasJugadas,
                partidasGanadas: data.partidasGanadas,
                porcentajeAciertos: data.preguntasRespondidas ? ((data.preguntasAcertadas / data.preguntasRespondidas) * 100).toFixed(2) : 0,
            };
        });

        // Ordenar el ranking por partidas ganadas (y en caso de empate por porcentaje de aciertos)
        ranking.sort((a, b) => {
            if (b.partidasGanadas === a.partidasGanadas) {
                return b.porcentajeAciertos - a.porcentajeAciertos;  // Si hay empate, se ordena por el porcentaje de aciertos
            }
            return b.partidasGanadas - a.partidasGanadas;  // Ordenar por partidas ganadas
        });

        // Limitar el número de jugadores que se van a mostrar
        const topRanking = ranking.slice(0, cantidad);

        // Enviar la respuesta con el ranking
        res.status(200).send(topRanking);

    } catch (error) {
        console.error("Error al obtener el ranking de jugadores:", error);
        res.status(500).send({ error: "Error al obtener el ranking de jugadores." });
    }
});




const server = app.listen(port, () => {
    console.log(`Servidor escuchando en el puerto ${port}`);
});

// Manejar errores de puerto en uso
server.on("error", (error) => {
    if (error.code === "EADDRINUSE") {
        console.error(`El puerto ${port} ya está en uso. Intentando reiniciar...`);
        setTimeout(() => {
            server.close();
            server.listen(port);
        }, 1000);
    } else {
        console.error("Error inesperado:", error);
    }
});




