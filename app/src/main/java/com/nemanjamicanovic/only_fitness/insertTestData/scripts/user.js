const admin = require("firebase-admin");
const serviceAccount = require("../serviceAccountKey.json");
const { users } = require("../data/users");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const auth = admin.auth();
const db = admin.firestore();

const protectedEmail = "micko1@gmail.com";
const passwordForAllUsers = "test123";


async function deleteAllUsers(nextPageToken) {
    console.log("---deleteAllUsers()---");

    const listUsersResult = await auth.listUsers(1000, nextPageToken);

    // Filter out protected user
    const usersToDelete = listUsersResult.users.filter(
        user => user.email !== protectedEmail
    );

    const uids = usersToDelete.map(user => user.uid);

    if (uids.length > 0) {
        // Delete from Authentication
        await auth.deleteUsers(uids);
        console.log(`Deleted ${uids.length} users from Authentication.`);

        // Delete from Firestore 'User' collection
        const batch = db.batch();
        uids.forEach(uid => {
            const docRef = db.collection("User").doc(uid);
            batch.delete(docRef);
        });

        await batch.commit();
        console.log(`Deleted ${uids.length} documents from 'User' collection.`);
    }
    else {
        console.log("No users to delete.");
    }

    if (listUsersResult.pageToken) {
        await deleteAllUsers(listUsersResult.pageToken);
    }
}


async function addUsers() {
    console.log("---addUsers()---");
    
    let usersCount = users.length;

    const batch = db.batch();
    for (const user of users) {
        try {
            const userRecord = await auth.createUser({
                email: user.email,
                password: passwordForAllUsers
            });
        
            const newDocRef = db.collection("User").doc(userRecord.uid);
            batch.set(newDocRef, user);
        }
        catch (error) {
            usersCount--;
            console.warn(`Failed for ${user.username}: `, error.message);
        }
    }
    
    await batch.commit();
    console.log(`Added ${usersCount} users.`);
}


deleteAllUsers()
    .then(() => console.log("deleteAllUsers() successfully done."))
    .catch(error => console.error("Error deleteAllUsers(): ", error))
    .then(() => {
        addUsers()
            .then(() => console.log("addUsers() successfully done."))
            .catch(error => console.error("Error addUsers(): ", error));
    });
