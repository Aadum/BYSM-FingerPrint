
/*
 * Copyright 2018 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fgtit.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Beneficiary extends RealmObject {
    @PrimaryKey
    @Required
    private String 	beneficiaryId;
    @Required
    private String 	firstname;
    @Required
    private String 	lastname;
    @Required
    private String 	ward;
    @Required
    private String 	lga;

    private Date	enroldate;
    private String	phone;

    private String	comment;
//    private String	cardsn;
//    private String 	template1;
//    private String 	template2;
//    private String  	photo;
//
//    private byte[] 	bytes1;
//    private byte[] 	bytes2;

    public String getBeneficiaryId() {
        return beneficiaryId;

    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;

    }


    public String getFirstname() {
        return firstname;

    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;

    }

    public String getLastname() {
        return lastname;

    }

    public void setLastname(String firstname) {
        this.lastname = lastname;

    }

    public String getWard() {
        return ward;

    }

    public void setWard(String ward) {
        this.ward = ward;

    }

    public String getLga() {
        return lga;

    }

    public void setLga(String lga) {
        this.lga =lga;

    }

    public Date getEnroldate() {
        return enroldate;

    }

    public void setEnroldate(Date enroldate) {
        this.enroldate = enroldate;

    }

    public String getPhone() {
        return phone;

    }

    public void setPhone(String phone) {
        this.phone = phone;

    }

    public String getComment() {
        return comment;

    }

    public void setComment(String comment) {
        this.comment = comment;

    }





    public Beneficiary() {
        this.beneficiaryId = UUID.randomUUID().toString();
        this.firstname = "";
        this.lastname = "";
        this.enroldate = new Date();
        this.ward = "";
        this.lga = "";
        this.phone = "";
        this.comment = "";
    }



}
