package com.smart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smart.Entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id =:userid")
	//current page= page
	// contacts per page
	public Page<Contact> findContactsByUser(int userid, Pageable pageable);
}
