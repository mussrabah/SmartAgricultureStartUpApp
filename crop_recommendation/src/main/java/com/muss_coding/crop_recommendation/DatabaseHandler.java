package com.muss_coding.crop_recommendation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {

    /**
     * TODO:
     * FIX IT:
     * In allocated array only one question is getting added, after that it overwriting the question.
     * */

    private static final String TAG = "FIRE_RESPONSE";

    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private Context context;

    public DatabaseHandler(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void addUserToFirestore(String userId, Map<String, Object> userData, final FirestoreCallback callback) {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User added with ID: " + userId);
                        callback.onCallback(true); // Return true for success
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding user", e);
                        callback.onCallback(false); // Return false for failure
                    }
                });
    }

    public void addQuestionToFirestore(String collectionName, Map<String, Object> data, final FirestoreCallback callback) {
        CollectionReference collectionReference = db.collection(collectionName);

        collectionReference.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Get the generated document ID
                        String documentId = documentReference.getId();

                        // Update the 'id' field with the document ID
                        Map<String, Object> updatedData = new HashMap<>(data);
                        updatedData.put("id", documentId);

                        // Update the document with the 'id' field
                        documentReference.set(updatedData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentId);
                                        updateAllocatedArray(documentReference);
                                        callback.onCallback(true); // Return true for success
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document with 'id' field", e);
                                        callback.onCallback(false); // Return false for failure
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        callback.onCallback(false); // Return false for failure
                    }
                });
    }

    // Update the allocated array of every user with the document reference of the newly created question
    public void updateAllocatedArray(DocumentReference questionRef) {
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<DocumentReference> allocatedArray = (List<DocumentReference>) document.get("allocatedArray");
                        if (allocatedArray == null) {
                            allocatedArray = new ArrayList<>();
                        }
                        // Add the new question reference if it's not already in the list
                        if (!allocatedArray.contains(questionRef)) {
                            allocatedArray.add(questionRef);

                            // Update the allocatedArray field in Firestore
                            document.getReference().update("allocatedArray", allocatedArray)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("DB_HANDLER", "Question reference added successfully");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("DB_HANDLER", "Error updating allocatedArray", e);
                                        }
                                    });
                        }
                    }
                } else {
                    // Handle errors
                    Toast.makeText(context, "Error getting users", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Callback interface for Firestore operations
    public interface FirestoreCallback {
        void onCallback(boolean isSuccess);
    }
    public void updateDataInFirestore(String collectionName, String documentId, Map<String, Object> data) {
        db.collection(collectionName)
                .document(documentId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        showToast("Data updated successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        showToast("Failed to update data.");
                    }
                });
    }

    public interface OnAnswerStoredListener {
        void onAnswerStored();
        void onFailure(Exception e);
    }

    public void storeAnswer(String user_id, String questionId, String question, String response, String reason, OnAnswerStoredListener listener) {
        Map<String, Object> answerData = new HashMap<>();
        answerData.put("userId", user_id);
        answerData.put("questionId", questionId);
        answerData.put("question", question);
        answerData.put("response", response);
        answerData.put("reason", reason);

        db.collection("answers")
                .add(answerData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Then, remove the question's reference from the user's 'allotedQuestion' array
                        removeQuestionFromAllotedQuestions(questionId, listener);
                        listener.onAnswerStored();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    public void deleteDataFromFirestore(String collectionName, String documentId) {
        db.collection(collectionName)
                .document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        showToast("Data deleted successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        showToast("Failed to delete data.");
                    }
                });
    }

    public void queryDataFromFirestore(String collectionName) {
        db.collection(collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                // Handle retrieved data here
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            showToast("Failed to retrieve data.");
                        }
                    }
                });
    }

    public void getAllocatedQuestionsInRealTime(String userId, final OnQuestionsRetrievedListener listener) {
        DocumentReference userRef = db.collection("users").document(userId);

        listenerRegistration = userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    listener.onFailure(e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    List<DocumentReference> allocatedArray = (List<DocumentReference>) snapshot.get("allocatedArray");
                    if (allocatedArray != null && !allocatedArray.isEmpty()) {
                        fetchQuestionsFromReferences(allocatedArray, listener);
                    } else {
                        listener.onQuestionsRetrieved(new ArrayList<>()); // Empty list
                    }
                } else {
                    listener.onFailure(new Exception("User document does not exist."));
                }
            }
        });
    }

    private void fetchQuestionsFromReferences(List<DocumentReference> allocatedArray, final OnQuestionsRetrievedListener listener) {
        final List<DocumentSnapshot> questionDocuments = new ArrayList<>();
        final TaskCompletionSource<List<DocumentSnapshot>> taskCompletionSource = new TaskCompletionSource<>();

        for (DocumentReference docRef : allocatedArray) {
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        questionDocuments.add(task.getResult());
                        if (questionDocuments.size() == allocatedArray.size()) {
                            taskCompletionSource.setResult(questionDocuments);
                        }
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                }
            });
        }

        taskCompletionSource.getTask().addOnCompleteListener(new OnCompleteListener<List<DocumentSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DocumentSnapshot>> task) {
                if (task.isSuccessful()) {
                    listener.onQuestionsRetrieved(task.getResult());
                } else {
                    listener.onFailure(task.getException());
                }
            }
        });
    }

    public void removeListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    public interface OnQuestionsRetrievedListener {
        void onQuestionsRetrieved(List<DocumentSnapshot> questions);
        void onFailure(Exception e);
    }

    private void removeQuestionFromAllotedQuestions(String questionId, OnAnswerStoredListener listener) {
        // Get the current user's document reference (assuming you have the logic to get the user's ID)
        String userId = getCurrentUserId(); // Implement this method to get the current user's ID
        DocumentReference userRef = db.collection("users").document(userId);

        // Use a transaction to update the user document
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userRef);
                List<DocumentReference> allotedQuestions = (List<DocumentReference>) snapshot.get("allocatedArray");
                if (allotedQuestions != null) {
                    allotedQuestions.remove(db.collection("questions").document(questionId));
                    transaction.update(userRef, "allocatedArray", allotedQuestions);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DatabaseHandler", "Question removed from allotedQuestions array");
                listener.onAnswerStored(); // Notify the listener that the answer is stored
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DatabaseHandler", "Error removing question from allotedQuestions array", e);
                listener.onFailure(e); // Notify the listener about the failure
            }
        });
    }

    public void getUserSpecificQuestionStatistics(String userId, OnQuestionStatisticsRetrievedListener listener) {
        CollectionReference answersRef = db.collection("answers");
        answersRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<QuestionStatistics> questionStatisticsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String questionId = document.getString("questionId");
                            String question = document.getString("question");
                            String response = document.getString("response");
                            String reason = document.getString("reason");

                            // Find existing question statistics or create a new one
                            QuestionStatistics stats = findQuestionStatistics(questionStatisticsList, questionId, question);
                            if (response.equals("YES")) {
                                stats.incrementYesCount();
                            } else if (response.equals("NO")) {
                                stats.incrementNoCount();
                            }
                            stats.addReason(reason);
                        }
                        listener.onQuestionStatisticsRetrieved(questionStatisticsList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    public interface OnUserRetrievedListener {
        void onUserRetrieved(User user);
        void onFailure(Exception e);
    }

    public void getUserByEmailAndPassword(String email, String password, OnUserRetrievedListener listener) {
        DocumentReference userRef = db.collection("users").document(email);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        user.setUserName(document.get("user_name").toString());
                        user.setPassword(document.get("password").toString());
                        user.setEmail(email);
                        user.setPoints(Integer.parseInt(document.get("points").toString()));

                        if (user != null && user.getPassword().equals(password)) {
                            listener.onUserRetrieved(user);
                        } else {
                            listener.onFailure(new Exception("Incorrect password"));
                        }
                    } else {
                        listener.onFailure(new Exception("User not found"));
                    }
                } else {
                    listener.onFailure(task.getException());
                }
            }
        });
    }



    private QuestionStatistics findQuestionStatistics(List<QuestionStatistics> list, String questionId, String question) {
        for (QuestionStatistics stats : list) {
            if (stats.getQuestionId().equals(questionId)) {
                return stats;
            }
        }
        QuestionStatistics newStats = new QuestionStatistics(questionId, question);
        list.add(newStats);
        return newStats;
    }

    public void getQuestionStatistics(final OnQuestionStatisticsRetrievedListener listener) {
        db.collection("answers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, QuestionStatistics> questionStatsMap = new HashMap<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String questionId = document.getString("questionId");
                                String question = document.getString("question");
                                String response = document.getString("response");
                                String reason = document.getString("reason");

                                Log.d("QUESTION", questionId + ", " + question + ", " + response + ", " + reason );

                                QuestionStatistics stats;
                                if (questionStatsMap.containsKey(questionId)) {
                                    stats = questionStatsMap.get(questionId);
                                } else {
                                    stats = new QuestionStatistics(questionId, question);
                                    questionStatsMap.put(questionId, stats);
                                }

                                if ("YES".equals(response)) {
                                    stats.incrementYesCount();
                                } else if ("NO".equals(response)) {
                                    stats.incrementNoCount();
                                }

                                if (reason != null && !reason.isEmpty()) {
                                    stats.addReason(reason);
                                }
                            }

                            List<QuestionStatistics> questionStatsList = new ArrayList<>(questionStatsMap.values());
                            listener.onQuestionStatisticsRetrieved(questionStatsList);
                        } else {
                            listener.onFailure(task.getException());
                        }
                    }
                });
    }

    public interface OnQuestionStatisticsRetrievedListener {
        void onQuestionStatisticsRetrieved(List<QuestionStatistics> questionStatistics);
        void onFailure(Exception e);
    }


    // Implement method to get the current user's ID
    private String getCurrentUserId() {
        // Implement logic to get the current user's ID
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_shared_pref", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        return email; // Replace this with actual user ID
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
