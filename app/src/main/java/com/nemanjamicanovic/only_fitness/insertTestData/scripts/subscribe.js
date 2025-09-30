const admin = require("firebase-admin");
const serviceAccount = require("../serviceAccountKey.json");
const { subscribes } = require("../data/subscribes");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();


async function deleteAllSubscribes() {
    console.log("---deleteAllSubscribes()---");
    
    const querySnapshot = await db.collection("Subscribe").get();
  
    if (querySnapshot.empty) {
        console.log("No documents in 'Subscribe' collection.");
        return;
    }
  
    const batch = db.batch();
    querySnapshot.docs.forEach(doc => {
        batch.delete(doc.ref);
    });
  
    await batch.commit();
    console.log(`Deleted ${querySnapshot.size} documents from 'Subscribe' collection.`);
}


async function addSubscribes() {
    console.log("---addSubscribes()---");

    const userQuerySnapshot = await db.collection("User").get();
    const usersByUsername = {};

    userQuerySnapshot.docs.forEach(doc => {
        const username = doc.get("username");
        usersByUsername[username] = doc.ref;
    });

    let subscribesCount = subscribes.length;

    const batch = db.batch();
    subscribes.forEach(({ from, to }) => {
        const fromRef = usersByUsername[from];
        const toRef = usersByUsername[to];

        if (fromRef && toRef) {
            const newDocRef = db.collection("Subscribe").doc();
            batch.set(newDocRef, {
                subscriptionFromRef: fromRef,
                subscribedToRef: toRef,
            });
        }
        else {
            subscribesCount--;
            console.warn(`Skipped: from='${from}' or to='${to}' not found.`);
        }
    });

    await batch.commit();
    console.log(`Added ${subscribesCount} documents to 'Subscribe' collection.`);
}


deleteAllSubscribes()
    .then(() => console.log("deleteAllSubscribes() successfully done."))
    .catch(error => console.error("Error deleteAllSubscribes(): ", error))
    .then(() => {
        addSubscribes()
            .then(() => console.log("addSubscribes() successfully done."))
            .catch(error => console.error("Error addSubscribes(): ", error));
    });
