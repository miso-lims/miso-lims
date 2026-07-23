package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.Hashtable;
import java.util.Objects;
import java.util.SortedSet;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.jspecify.annotations.Nullable;
import org.springframework.ldap.core.DirContextOperations;

/**
 * Wrapper class to work around Spring Security 7.1.0 bug that makes uid required. This class simply
 * returns the username in place of uid if missing.
 * https://github.com/spring-projects/spring-security/issues/19370
 */
public class UidFallbackDirContextAdapter implements DirContextOperations {

  private final DirContextOperations base;
  private final String username;

  public UidFallbackDirContextAdapter(DirContextOperations base, String username) {
    this.base = base;
    this.username = username;
  }

  @Override
  public Attributes getAttributes(Name name) throws NamingException {
    return base.getAttributes(name);
  }

  @Override
  public Attributes getAttributes(String name) throws NamingException {
    return base.getAttributes(name);
  }

  @Override
  public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
    return base.getAttributes(name, attrIds);
  }

  @Override
  public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
    return base.getAttributes(name, attrIds);
  }

  @Override
  public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws NamingException {
    base.modifyAttributes(name, mod_op, attrs);
  }

  @Override
  public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException {
    base.modifyAttributes(name, mod_op, attrs);
  }

  @Override
  public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
    base.modifyAttributes(username, mods);
  }

  @Override
  public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
    base.modifyAttributes(name, mods);
  }

  @Override
  public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
    base.bind(name, obj, attrs);
  }

  @Override
  public void bind(String name, Object obj, Attributes attrs) throws NamingException {
    base.bind(name, obj, attrs);
  }

  @Override
  public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
    base.rebind(name, obj, attrs);
  }

  @Override
  public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
    base.rebind(name, obj, attrs);
  }

  @Override
  public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
    return base.createSubcontext(username, attrs);
  }

  @Override
  public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
    return base.createSubcontext(username, attrs);
  }

  @Override
  public DirContext getSchema(Name name) throws NamingException {
    return base.getSchema(name);
  }

  @Override
  public DirContext getSchema(String name) throws NamingException {
    return base.getSchema(name);
  }

  @Override
  public DirContext getSchemaClassDefinition(Name name) throws NamingException {
    return base.getSchemaClassDefinition(name);
  }

  @Override
  public DirContext getSchemaClassDefinition(String name) throws NamingException {
    return base.getSchemaClassDefinition(name);
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn)
      throws NamingException {
    return base.search(username, matchingAttributes, attributesToReturn);
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn)
      throws NamingException {
    return base.search(username, matchingAttributes, attributesToReturn);
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
    return base.search(username, matchingAttributes);
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
    return base.search(username, matchingAttributes);
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
    return base.search(name, filter, cons);
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons)
      throws NamingException {
    return base.search(name, filter, cons);
  }

  @Override
  public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons)
      throws NamingException {
    return base.search(name, filterExpr, filterArgs, cons);
  }

  @Override
  public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs,
      SearchControls cons)
      throws NamingException {
    return base.search(name, filterExpr, filterArgs, cons);
  }

  @Override
  public Object lookup(Name name) throws NamingException {
    return base.lookup(name);
  }

  @Override
  public Object lookup(String name) throws NamingException {
    return base.lookup(name);
  }

  @Override
  public void bind(Name name, Object obj) throws NamingException {
    base.bind(name, obj);
  }

  @Override
  public void bind(String name, Object obj) throws NamingException {
    base.bind(name, obj);
  }

  @Override
  public void rebind(Name name, Object obj) throws NamingException {
    base.rebind(name, obj);
  }

  @Override
  public void rebind(String name, Object obj) throws NamingException {
    base.rebind(name, obj);
  }

  @Override
  public void unbind(Name name) throws NamingException {
    base.unbind(name);
  }

  @Override
  public void unbind(String name) throws NamingException {
    base.unbind(name);
  }

  @Override
  public void rename(Name oldName, Name newName) throws NamingException {
    base.rename(oldName, newName);
  }

  @Override
  public void rename(String oldName, String newName) throws NamingException {
    base.rename(oldName, newName);
  }

  @Override
  public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
    return base.list(name);
  }

  @Override
  public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
    return base.list(name);
  }

  @Override
  public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
    return base.listBindings(name);
  }

  @Override
  public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
    return base.listBindings(name);
  }

  @Override
  public void destroySubcontext(Name name) throws NamingException {
    base.destroySubcontext(name);
  }

  @Override
  public void destroySubcontext(String name) throws NamingException {
    base.destroySubcontext(name);
  }

  @Override
  public Context createSubcontext(Name name) throws NamingException {
    return base.createSubcontext(name);
  }

  @Override
  public Context createSubcontext(String name) throws NamingException {
    return base.createSubcontext(name);
  }

  @Override
  public Object lookupLink(Name name) throws NamingException {
    return base.lookupLink(name);
  }

  @Override
  public Object lookupLink(String name) throws NamingException {
    return base.lookupLink(name);
  }

  @Override
  public NameParser getNameParser(Name name) throws NamingException {
    return base.getNameParser(name);
  }

  @Override
  public NameParser getNameParser(String name) throws NamingException {
    return base.getNameParser(name);
  }

  @Override
  public Name composeName(Name name, Name prefix) throws NamingException {
    return base.composeName(name, prefix);
  }

  @Override
  public String composeName(String name, String prefix) throws NamingException {
    return base.composeName(name, prefix);
  }

  @Override
  public Object addToEnvironment(String propName, Object propVal) throws NamingException {
    return base.addToEnvironment(propName, propVal);
  }

  @Override
  public Object removeFromEnvironment(String propName) throws NamingException {
    return base.removeFromEnvironment(propName);
  }

  @Override
  public Hashtable<?, ?> getEnvironment() throws NamingException {
    return base.getEnvironment();
  }

  @Override
  public void close() throws NamingException {
    base.close();
  }

  @Override
  public @Nullable String getStringAttribute(String name) {
    String value = base.getStringAttribute(name);
    if (value == null && Objects.equals(name, "uid")) {
      value = username;
    }
    return value;
  }

  @Override
  public @Nullable Object getObjectAttribute(String name) {
    return base.getObjectAttribute(name);
  }

  @Override
  public boolean attributeExists(String name) {
    return base.attributeExists(name);
  }

  @Override
  public void setAttributeValue(String name, @Nullable Object value) {
    base.setAttributeValue(name, value);
  }

  @Override
  public void setAttributeValues(String name, Object @Nullable [] values) {
    base.setAttributeValues(name, values);
  }

  @Override
  public void setAttributeValues(String name, Object @Nullable [] values, boolean orderMatters) {
    base.setAttributeValues(name, values, orderMatters);
  }

  @Override
  public void addAttributeValue(String name, Object value) {
    base.addAttributeValue(name, value);
  }

  @Override
  public void addAttributeValue(String name, Object value, boolean addIfDuplicateExists) {
    base.addAttributeValue(name, value, addIfDuplicateExists);
  }

  @Override
  public void removeAttributeValue(String name, Object value) {
    base.removeAttributeValue(name, value);
  }

  @Override
  public String @Nullable [] getStringAttributes(String name) {
    String[] values = base.getStringAttributes(name);
    if (values == null || values.length == 0) {
      values = new String[] {username};
    }
    return values;
  }

  @Override
  public Object @Nullable [] getObjectAttributes(String name) {
    return base.getObjectAttributes(name);
  }

  @Override
  public @Nullable SortedSet<String> getAttributeSortedStringSet(String name) {
    return base.getAttributeSortedStringSet(name);
  }

  @Override
  public Name getDn() {
    return base.getDn();
  }

  @Override
  public Attributes getAttributes() {
    return base.getAttributes();
  }

  @Override
  public ModificationItem[] getModificationItems() {
    return base.getModificationItems();
  }

  @Override
  public boolean isUpdateMode() {
    return base.isUpdateMode();
  }

  @Override
  public String[] getNamesOfModifiedAttributes() {
    return base.getNamesOfModifiedAttributes();
  }

  @Override
  public void update() {
    base.update();
  }

  @Override
  public void setDn(Name dn) {
    base.setDn(dn);
  }

  @Override
  public String getNameInNamespace() {
    return base.getNameInNamespace();
  }

  @Override
  public String getReferralUrl() {
    return base.getReferralUrl();
  }

  @Override
  public boolean isReferral() {
    return base.isReferral();
  }

}
