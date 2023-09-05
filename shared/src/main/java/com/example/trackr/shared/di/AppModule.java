package com.example.trackr.shared.di;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.trackr.shared.data.SeedData;
import com.example.trackr.shared.db.AppDatabase;
import com.example.trackr.shared.db.dao.TaskDao;
import com.example.trackr.shared.db.tables.User;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Rameel Hassan
 * Created 03/03/2023 at 11:02 AM
 */
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    private AppDatabase appDatabase;
    private Clock clock;

    @Provides
    @Singleton
    AppDatabase provideDatabase(@ApplicationContext Context context) {
        appDatabase = Room.databaseBuilder(
                context,
                AppDatabase.class,
                "tracker-db"
        ).fallbackToDestructiveMigration()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
                        ioExecutor.execute(() -> {
                            insertSeedData();
                            ioExecutor.shutdown(); // Don't forget to shut down the executor when done
                        });

                    }
                })
            .build();
        return appDatabase;
    }

    private void insertSeedData() {
        appDatabase.taskDao().insertUsers(SeedData.Users);
        appDatabase.taskDao().insertTags(SeedData.Tags);
        appDatabase.taskDao().insertTasks(SeedData.Tasks);
        appDatabase.taskDao().insertTaskTags(SeedData.TaskTags);
        appDatabase.taskDao().insertUserTasks(SeedData.UserTasks);
    }

    @Provides
    User provideCurrentUser() {
        // For simplicity of sample, assign first user as current user
        return SeedData.Users.get(0);
    }

    @Provides
    TaskDao provideTaskDao(AppDatabase appDatabase) {
        return appDatabase.taskDao();
    }

    @Provides
    @Singleton
    Clock provideClock() {
        clock = Clock.systemDefaultZone();
        return clock;
    }

}
