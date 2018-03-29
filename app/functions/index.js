const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

const SENDGRID_API_KEY = functions.config().sendgrid.key;

const sgMail = require('@sendgrid/mail');
sgMail.setApiKey(SENDGRID_API_KEY);

exports.firestoreEmail = functions.firestore
    .document('users/{userId}')
    .onCreate(event => {
        const userId = event.params.userId;
        const db = admin.firestore();

        return db.collection('users').doc(userId)
                .get()
                .then(doc => {
                    const user = doc.data();

                    const msg = {
                        to: user.email,
                        from: 'hello@mail.ayahya.me',
                        subject: 'Welcome!',

                        templateId: '3b0a203a-5cd0-4be2-a2e5-5ec8abb3023b',
                        substitutionWrappers: ['{{', '}}'],
                        substitutions: {
                            name: user.name
                        }

                    };
                    return sgMail.send(msg);
                })
                .then(() => console.log('Email sent!'))
                .catch(err => console.log(err))
    });