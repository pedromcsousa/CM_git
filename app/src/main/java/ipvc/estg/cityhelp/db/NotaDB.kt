package ipvc.estg.cityhelp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.estg.cityhelp.dao.NotaDAO
import kotlinx.coroutines.CoroutineScope
import ipvc.estg.cityhelp.entities.Nota
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Nota::class), version = 1, exportSchema = false)
public abstract class NotaDB : RoomDatabase() {

    abstract fun NotaDao(): NotaDAO

    private class NotaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var notaDao = database.NotaDao()

                    notaDao.deleteAll()

                    var nota = Nota(1, "Titulo 1", "Descrição de nota 1",
                        java.util.Calendar.getInstance().toString()
                    )
                    notaDao.insert(nota)

                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NotaDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotaDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotaDB::class.java,
                    "notas_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(NotaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }

}