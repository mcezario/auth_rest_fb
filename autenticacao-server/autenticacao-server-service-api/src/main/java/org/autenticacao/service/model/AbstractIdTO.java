package org.autenticacao.service.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 *
 * @author mcezario
 *
 */
@MappedSuperclass
public abstract class AbstractIdTO {

    // atributos
    /**
     *
     */
    protected Long id;

    /**
     * @return ...
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

   /**
    * @param id
    */
   public void setId(Long id) {
       this.id = id;
   }
   
   @Transient
   public String getCode() {
	   if (this.id == null) {
		   return "";
	   }
	   StringBuilder code = new StringBuilder(String.valueOf(this.id));
	   for (int i = code.length(); i < 7; i++) {
		   code.insert(0, "0");
	   }
	   return code.toString();
   }
   
}
