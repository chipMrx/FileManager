package com.apcc.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apcc.emma.BuildConfig
import com.apcc.api.BasicAuthInterceptor
import com.apcc.api.ApiService
import com.apcc.framework.db.AppDB
import com.apcc.framework.db.AppDao
import com.apcc.utils.DataHelper
import com.apcc.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideEmmaService(): ApiService {
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG){
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logInterceptor)
        }
        httpClient.addInterceptor(BasicAuthInterceptor())
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)

        return Retrofit.Builder()
            .baseUrl(DataHelper.getServerApi())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(httpClient.build())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDB {
        return Room.databaseBuilder(app, AppDB::class.java, "emma.db")
            .fallbackToDestructiveMigration()
            //.addMigrations(MIGRATION_1_2)
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(db: AppDB): AppDao {
        return db.emmaDao()
    }

    ///////////////////////////////////////////////////////
    /*SQLite doesn't support column deletion straight away.
    *
    *
    * CREATE TABLE accounts_backup(serverId VARCHAR, accountId VARCHAR, firstname VARCHAR, lastname VARCHAR, email VARCHAR, active VARCHAR);
    * INSERT INTO accounts_backup SELECT serverId, accountId, firstname, lastname, email, active FROM accounts;
    * DROP TABLE accounts;
    * CREATE TABLE accounts(serverId VARCHAR, accountId VARCHAR, firstname VARCHAR, lastname VARCHAR, email VARCHAR, active VARCHAR);
    * INSERT INTO accounts SELECT serverId, accountId, firstname, lastname, email, active FROM accounts_backup;
    * DROP TABLE accounts_backup;
    * */
    private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

//            database.execSQL("""
//                CREATE TABLE accounts_backup(serverId VARCHAR, accountId VARCHAR, firstname VARCHAR, lastname VARCHAR, email VARCHAR, active VARCHAR)
//                """.trimIndent()
//            )
//            database.execSQL("""
//                INSERT INTO new_Song (id, name, tag)
//                SELECT id, name, tag FROM Song
//                """.trimIndent()
//            )
        }
    }


//    private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL(
//                "CREATE TABLE `Fruit` (`id` INTEGER, "
//                        + "`name` TEXT, PRIMARY KEY(`id`))"
//
//            database.execSQL("ALTER TABLE Book "
//                    + " ADD COLUMN pub_year INTEGER")
//            )
//        }
//    }

}
