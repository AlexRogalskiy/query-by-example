package ch.frankel.blog.qbe

import org.hibernate.Session
import org.hibernate.criterion.Example
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.criteria.Predicate
import javax.servlet.annotation.WebServlet
import javax.servlet.http.*

@Entity
class Person(@Id @GeneratedValue var id: Long? = null,
             val firstName: String?, val lastName: String?, val birthdate: LocalDate?)

@WebServlet("/jpql")
class JpqlServlet : HttpServlet() {
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        doAndDispatch(req, resp) { firstName, lastName, birthdate, em ->
            val select = "SELECT p FROM Person p"
            val jpql = if (firstName.isBlank() && lastName.isBlank() && birthdate == null) select
            else {
                val where = "$select WHERE"
                var expression = where
                if (firstName.isNotBlank())
                    expression += " firstName = '$firstName'"
                if (lastName.isNotBlank()) {
                    if (expression != where)
                        expression += " AND"
                    expression += " lastName = '$lastName'"
                }
                if (birthdate != null) {
                    if (expression != where)
                        expression += " AND"
                    expression += " birthdate = '$birthdate'"
                }
                expression
            }
            val cq = em.createQuery(jpql)
            cq.resultList
        }
    }
}

@WebServlet("/criteria")
class CriteriaServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        doAndDispatch(req, resp) { firstName, lastName, birthdate, em ->
            val cq = em.criteriaBuilder.createQuery(Person::class.java)
            val person = cq.from(Person::class.java)
            var predicates = listOf<Predicate>()
            if (firstName.isNotBlank())
                predicates = predicates + em.criteriaBuilder.equal(person.get<String>("firstName"), firstName)
            if (lastName.isNotBlank())
                predicates  = predicates + em.criteriaBuilder.equal(person.get<String>("lastName"), lastName)
            if (birthdate != null)
                predicates = predicates + em.criteriaBuilder.equal(person.get<LocalDate>("birthdate"), birthdate)
            cq.where(*predicates.toTypedArray())
            val query = em.createQuery(cq)
            query.resultList
        }
    }
}

@WebServlet("/qbe")
class QbeServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        doAndDispatch(req, resp) { firstName, lastName, birthdate, em ->
            val session = em.delegate as Session
            val person = Person(firstName = if (firstName.isBlank()) null else firstName,
                lastName = if (lastName.isBlank()) null else lastName,
                birthdate = birthdate)
            val example = Example.create(person)
            val criteria = session.createCriteria(Person::class.java).add(example)
            criteria.list()
        }
    }
}

private fun doAndDispatch(req: HttpServletRequest, resp: HttpServletResponse,
                          f: (String, String, LocalDate?, EntityManager) -> List<*>) {
    fun String.toLocaleDate() : LocalDate? = if (this.isBlank()) null
    else LocalDate.parse(this)
    val firstName = req.getParameter("firstName")
    val lastName = req.getParameter("lastName")
    val birthdate = req.getParameter("birthdate")?.toLocaleDate()
    val em = Persistence.emf.createEntityManager()
    val persons = f(firstName, lastName, birthdate, em)
    req.setAttribute("persons", persons)
    req.setAttribute("firstName", firstName)
    req.setAttribute("lastName", lastName)
    req.setAttribute("birthdate", birthdate)
    req.getRequestDispatcher("/WEB-INF/persons.jsp").forward(req, resp)
}