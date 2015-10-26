package org.autenticacao.service.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;

import org.autenticacao.service.dao.exception.DaoRuntimeException;
import org.autenticacao.service.model.AbstractIdTO;

/**
 * Classe abstrata das regras e operações de acesso a dados.
 *
 * Todas os metodos desta classe sobem a excecão não verificada
 * DaoRuntimeException, caso ocorra algum erro.
 *
 * @author Maikon
 *
 * @param <T>
 *            A entidade
 */
public abstract class GenericDao<T> {

	@PersistenceContext(unitName = "autenticacaoPU")
	private EntityManager entityManager;
    
	public void detach(Object obj) {
    	this.entityManager.detach(obj);
    }
    
	/**
	 * Carrega a entidade pelo seu ID.
	 * 
	 * @param id
	 *            O id da entidade
	 * 
	 * @return A entidade
	 */
	public T load(Long id) {
        if (id == null) {
            throw new NullPointerException("O id não deve ser nulo. Entidade[" + this.getNameEntity() + "]");
        }
        T object = null;
        try {
            object = (T) this.entityManager.find(this.getEntity(), id);
        } catch (NoResultException e) {
            object = null;  
        } catch (EntityNotFoundException e) {
        	object = null;
		} catch (Exception e) {
            throw new DaoRuntimeException("Erro ao recuperar o objeto. id = " + id, this.getNameEntity(), e);
        }
        return object;
    }

	/**
	 * Carrega a entidade pelo sua chave composta.
	 * 
	 * @param pk
	 *            A chave composta.
	 * 
	 * @return A entidade
	 */
	public T loadByPK(Serializable pk) {
        if (pk == null) {
            throw new NullPointerException("A chave nao deve ser nula. Entidade[" + this.getNameEntity() + "]");
        }
        T object = null;
        try {
            object = (T) this.entityManager.find(this.getEntity(), pk);
        } catch (NoResultException e) {
            object = null;
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao recuperar o objeto. pk = " + pk, this.getNameEntity(), e);
        }
        return object;
    }

	/**
	 * Obtem a lista da Entidade.
	 * 
	 * @return A lista de registros
	 */
    @SuppressWarnings("unchecked")
    public Set<T> list() {
        Set<T> lstObj = null;
        try {
            String hql = "From " + this.getEntity().getSimpleName();
            javax.persistence.Query query = this.entityManager.createQuery(hql);
            lstObj = new LinkedHashSet<T>(query.getResultList());
        } catch (NoResultException e) {
            lstObj = null;
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao listar dados do objeto.", this.getNameEntity(), e);
        }
        return lstObj;
    }

	/**
	 * Obtem a lista da Entidade dentro de um determinado range.
	 * 
	 * @param min
	 *            Range inicial
	 * @param max
	 *            Range final
	 * 
	 * @return A lista da entidade
	 */
    @SuppressWarnings("unchecked")
    public List<T> list(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("O valor do resultado minimo e "
                    + "maior que o valor do resultado maximo. " + "Entidade[" + this.getNameEntity() + "]");
        }
        List<T> lstObj = null;
        try {
            String hql = "From " + this.getEntity().getSimpleName();
            javax.persistence.Query query = this.entityManager.createQuery(hql);
            query.setFirstResult(min);
            query.setMaxResults(max);
            lstObj = new ArrayList<T>(query.getResultList());
        } catch (NoResultException e) {
            lstObj = null;
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao listar dados do objeto. " + "Valor Min.[" + min + "], Valor Max["
                    + max + "]", this.getNameEntity(), e);
        }
        return lstObj;
    }

	/**
	 * Insere a entidade.
	 * 
	 * @param entity
	 *            A entidade
	 * @return A entidade inserida
	 */
    public Long insert(T entity) {
        if (entity == null) {
            throw new NullPointerException("A Entidade não deve ser nula. " + "Entidade[" + this.getNameEntity() + "]");
        }
        Long id = null;
        try {
        	this.entityManager.persist(entity);
            if (entity instanceof AbstractIdTO) {
                Object idEntity = ((AbstractIdTO) entity).getId();
                if (idEntity instanceof Long) {
                    id = (Long) idEntity;
                }
            }
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao inserir", this.getNameEntity(), e);
        }
        return id;
    }

	/**
	 * Atualiza a entidade.
	 * 
	 * @param entity
	 *            A entidade
	 */
    public void update(T entity) {
        if (entity == null) {
            throw new NullPointerException("A Entidade não deve ser nula. " + "Entidade[" + this.getNameEntity() + "]");
        }
        try {
        	this.entityManager.merge(entity);
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao atualizar", this.getNameEntity(), e);
        }
    }

	/**
	 * Deleta a entidade.
	 * 
	 * @param entity
	 *            A entidade
	 */
    public void delete(T entity) {
        if (entity == null) {
            throw new NullPointerException("A Entidade não deve ser nula. " + "Entidade[" + this.getNameEntity() + "]");
        }
        try {
        	this.entityManager.remove(entity);
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao deletar", this.getNameEntity(), e);
        }
    }

	/**
	 * Obtem o total de registros da entidade.
	 * 
	 * @return O total de registros
	 */
    public int count() {
        int count = 0;
        try {
            String hql = "Select Count(*) From " + this.getEntity().getName() + " As tb ";
            javax.persistence.Query query = this.entityManager.createQuery(hql);
            Long cnt = (Long) query.getSingleResult();
            count = cnt.intValue();
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao recuperar o total de registros do objeto.", this.getNameEntity(), e);
        }
        return count;
    }

    /**
     * Obtem o resultado da query.
     *
     * @param sqlQuery
     *            A query
     * @param parameters
     *            Os parametros da query
     *
     * @return A query
     *
     * @throws DaoRuntimeException
     *             caso aconteça algum problema no acesso a dados
     */
    @SuppressWarnings("unchecked")
    public T getQueryUnique(String sqlQuery, Object... parameters) throws DaoRuntimeException {
        if ((sqlQuery == null) || (parameters == null)) {
            throw new NullPointerException("A namedQuery e os parametros nao devem ser nulos. " + "Entidade["
                    + this.getNameEntity() + "]");
        }
        T object = null;
        try {
            int i = 0;
            javax.persistence.Query query = this.entityManager.createQuery(sqlQuery);
            for (String paramName : getParametrosQuery(sqlQuery)) {
                query.setParameter(paramName, parameters[i]);
                i++;
            }
            object = (T) query.getSingleResult();
        } catch (NoResultException e) {
            object = null;
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao executar a query[" + sqlQuery + "], " + "params[" + parameters
                    + "].", this.getNameEntity(), e);
        }
        return object;
    }

    /**
     * Obtem o total de registros da query.
     *
     * @param sqlQuery
     *            A query
     * @param parameters
     *            Os parametros da query
     *
     * @return O total de registros
     *
     * @throws DaoRuntimeException
     *             caso aconteca algum problema no acesso a dados
     */
    public Long getQueryCount(String sqlQuery, Object... parameters) throws DaoRuntimeException {
        if ((sqlQuery == null) || (parameters == null)) {
            throw new NullPointerException("A namedQuery e os parametros não devem ser nulos. " + "Entidade["
                    + this.getNameEntity() + "]");
        }
        Long total = null;
        try {
            int i = 0;
            javax.persistence.Query query = this.entityManager.createQuery(sqlQuery);
            for (String paramName : getParametrosQuery(sqlQuery)) {
                query.setParameter(paramName, parameters[i]);
                i++;
            }
            total = (Long) query.getSingleResult();
        } catch (NoResultException e) {
            total = new Long(0);
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao executar a query[" + sqlQuery + "], " +
            		"params[" + parameters + "].", this.getNameEntity(), e);
        }
        return total;
    }

    /**
     * Obtem o resultado da query para um resultado de registros máximo.
     *
     * @param sqlQuery
     *            A query
     * @param maxResult
     *            O total de registros a ser retornado
     *
     * @return A lista de registros
     *
     * @throws DaoRuntimeException
     *             caso aconteça algum problema no acesso a dados
     */
    public List<T> getQueryListPager(String sqlQuery, int maxResult) throws DaoRuntimeException {
        return this.getQueryList(sqlQuery, new Object[]{ 0, maxResult });
    }

    /**
     * Obtem o resultado da query para um resultado de registros máximo.
     *
     * @param sqlQuery
     *            A query
     * @param maxResult
     *            O total de registros a ser retornado
     * @param parameters
     *            Os parametros da query
     *
     * @return A lista de registros
     *
     * @throws DaoRuntimeException
     *             caso aconteca algum problema no acesso a dados
     */
    public List<T> getQueryListPager(String sqlQuery, int maxResult, Object... parameters) {
        return this.getQueryList(sqlQuery, 0, maxResult, parameters);
    }

    /**
     * Obtem o resultado da query para um range de registros.
     *
     * @param sqlQuery
     *            A query
     * @param firstResult
     *            O range inicial dos registros
     * @param maxResult
     *            O range final dos registros
     * @param parameters
     *            Os parametros da query
     *
     * @return A lista de registros
     *
     * @throws DaoRuntimeException
     *             caso aconteça algum problema no acesso a dados
     */
    public List<T> getQueryListPager(String sqlQuery, int firstResult, int maxResult, Object... parameters) {
        return this.getQueryList(sqlQuery, firstResult, maxResult, parameters);
    }

    /**
     * Obtem o resultado da query.
     *
     * @param sqlQuery
     *            A query
     *
     * @return A lista de registros
     *
     * @throws DaoRuntimeException
     *             caso aconteça algum problema no acesso a dados
     */
    public List<T> getQueryList(String sqlQuery) throws DaoRuntimeException {
        return this.getQueryList(sqlQuery, 0, 0, new Object[] { null });
    }

    /**
     * Obtem o resultado da query.
     *
     * @param sqlQuery
     *            A query
     * @param parameters
     *            Os parametros da query
     *
     * @return A lista de registros.
     *
     * @throws DaoRuntimeException
     *             caso aconteça algum problema no acesso a dados
     */
    public List<T> getQueryList(String sqlQuery, Object... parameters) throws DaoRuntimeException {
        return this.getQueryList(sqlQuery, 0, 0, parameters);
    }
    
    protected CriteriaBuilder getCriteriaBuilder() {
    	return this.entityManager.getCriteriaBuilder();
    }
    
    protected EntityManager getEntityManager() {
    	return this.entityManager;
    }

    /*
     * Obtem o resultado da query para um determinado range.
     */
    @SuppressWarnings("unchecked")
    private List<T> getQueryList(String sqlQuery, int firstResult, int maxResult, Object... parameters)
            throws DaoRuntimeException {
    	List<T> lst = null;
        try {
            int i = 0;
            javax.persistence.Query query = this.entityManager.createQuery(sqlQuery);
            if (parameters != null) {
                for (String paramName : getParametrosQuery(sqlQuery)) {
                    query.setParameter(paramName, parameters[i]);
                    i++;
                }
                if (maxResult > 0) {
                    query.setMaxResults(maxResult);
                    if (firstResult != 0) {
                        query.setFirstResult(maxResult);
                    }
                }
                lst = new ArrayList<T>(query.getResultList());
            }
        } catch (NoResultException e) {
            lst = null;
        } catch (Exception e) {
            throw new DaoRuntimeException("Erro ao executar a query[" + sqlQuery + "], " + "" + "params["
                    + parameters.toString() + "].", this.getNameEntity(), e);
        }
        return lst;
    }

    /*
     * Solucao de contorno para ordenar os parametros de determinada
     * queryString
     */
    private Set<String> getParametrosQuery(String queryString) {
        Set<String> parameters = new LinkedHashSet<String>();
        Pattern p = Pattern.compile(":[\\w]+");
        Matcher math = p.matcher(queryString);
        while (math.find()) {
            parameters.add(math.group().substring(1));
        }
        return parameters;
    }

    /*
     * Obtem o nome da entidade
     */
    private String getNameEntity() {
        return this.getEntity().getSimpleName();
    }

	@SuppressWarnings("unchecked")
    private Class<T> getEntity() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}