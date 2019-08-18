package ch.frankel.blog.qbe

import java.time.LocalDate
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

object Persistence {
    val emf = javax.persistence.Persistence.createEntityManagerFactory("qbe")
}

@WebListener
class PersonInitializer : ServletContextListener {

    override fun contextInitialized(sce: ServletContextEvent) {
        val tableDdl = """
            CREATE TABLE PERSON ( 
                ID LONG NOT NULL AUTO_INCREMENT, 
                FIRST_NAME VARCHAR(50) NOT NULL, 
                LAST_NAME VARCHAR(50) NOT NULL, 
                BIRTHDATE DATE, 
            );""".trimIndent()
        val em = Persistence.emf.createEntityManager()
        em.transaction.begin()
        em.createNativeQuery(tableDdl)
        em.persist(Person(firstName = "John", lastName = "Doe", birthdate = LocalDate.of(1970, 1, 1)))
        em.persist(Person(firstName = "Jane", lastName = "Doe", birthdate = LocalDate.of(1980, 1, 1)))
        em.persist(Person(firstName = "John", lastName = "Dude", birthdate = LocalDate.of(1990, 10, 10)))
        em.transaction.commit()
    }

    override fun contextDestroyed(sce: ServletContextEvent) {
        // NOTHING TO DO
    }
}