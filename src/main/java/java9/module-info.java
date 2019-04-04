module com.autoepm.slf4j.jcdp {
    requires org.slf4j;
    requires JCDP;
    provides org.slf4j.spi.SLF4JServiceProvider with com.autoepm.slf4j.jcdp.JcdpProvider;
}