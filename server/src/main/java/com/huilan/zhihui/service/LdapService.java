

package com.huilan.zhihui.service;

import com.huilan.core.exception.ServerException;
import com.huilan.zhihui.model.LdapPerson;
import com.huilan.zhihui.model.User;

public interface LdapService {

    boolean existLdapServer();

    /**
     * 查找 ldap 用户
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    LdapPerson findByUsername(String username, String password);


    User registPerson(LdapPerson ldapPerson) throws ServerException;
}
