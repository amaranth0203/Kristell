package com.mycompany.kristell.DAO;

import java.util.List;
import com.mycompany.kristell.DAO.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "CARD".
 */
public class Card {

    private Long id;
    private Double Balance;
    private java.util.Date CreateTime;
    private String Comments;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CardDao myDao;

    private List<Transaction> transactionList;

    public Card() {
    }

    public Card(Long id) {
        this.id = id;
    }

    public Card(Long id, Double Balance, java.util.Date CreateTime, String Comments) {
        this.id = id;
        this.Balance = Balance;
        this.CreateTime = CreateTime;
        this.Comments = Comments;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCardDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return Balance;
    }

    public void setBalance(Double Balance) {
        this.Balance = Balance;
    }

    public java.util.Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(java.util.Date CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Transaction> getTransactionList() {
        if (transactionList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TransactionDao targetDao = daoSession.getTransactionDao();
            List<Transaction> transactionListNew = targetDao._queryCard_TransactionList(id);
            synchronized (this) {
                if(transactionList == null) {
                    transactionList = transactionListNew;
                }
            }
        }
        return transactionList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTransactionList() {
        transactionList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
