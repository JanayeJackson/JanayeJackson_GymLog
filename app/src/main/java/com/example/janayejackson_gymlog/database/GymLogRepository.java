package com.example.janayejackson_gymlog.database;

import android.app.Application;
import android.util.Log;

import com.example.janayejackson_gymlog.database.entities.GymLog;
import com.example.janayejackson_gymlog.MainActivity;
import com.example.janayejackson_gymlog.database.entities.User;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GymLogRepository {
    private final GymLogDAO gymLogDAO;
    private final UserDAO userDao;
    private ArrayList<GymLog> allLogs;

    private static GymLogRepository repository;

    private GymLogRepository(Application application) {
        GymLogDatabase db = GymLogDatabase.getDatabase(application);
        this.gymLogDAO = db.gymLogDAO();
        this.userDao = db.userDAO();
        this.allLogs = (ArrayList<GymLog>) this.gymLogDAO.getAllRecords();
    }

    public static GymLogRepository getRepository(Application application){
        if (repository != null){
            return repository;
        }
        Future<GymLogRepository> future = GymLogDatabase.databaseWriteExecutor.submit(
                new Callable<GymLogRepository>() {
                    @Override
                    public GymLogRepository call() throws Exception {
                        return new GymLogRepository(application);
                    }
                }
        );
        try{
            return future.get();
        }catch(InterruptedException | ExecutionException e){
            Log.d(MainActivity.TAG, "Problem getting GymLogRepository, thread error");
        }
        return null;
    }

    public ArrayList<GymLog> getAllLogs(){
        Future<ArrayList<GymLog>> future = GymLogDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<GymLog>>() {
                    @Override
                    public ArrayList<GymLog> call() throws Exception {
                        return (ArrayList<GymLog>) gymLogDAO.getAllRecords();
                    }
                });
        try{
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.i(MainActivity.TAG, "Problem when getting all GymLogs in the repository");
        }
        return null;
    }

    public void insertGymLog(GymLog gymLog) {
        GymLogDatabase.databaseWriteExecutor.execute(() -> {
            gymLogDAO.insert(gymLog);
        });
    }

    public void insertUser(User... user) {
        GymLogDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public User getUserBuUserName(String username) {
        Future<User> future = GymLogDatabase.databaseWriteExecutor.submit(
                new Callable<User>() {
                    @Override
                    public User call() throws Exception{
                        return userDao.getUserByUsername(username);
                    }
                });
        try{
            future.get();
        }catch(InterruptedException | ExecutionException e){
            Log.i(MainActivity.TAG, "Problem when getting user in the repository");
        }
        return null;
    }
}
