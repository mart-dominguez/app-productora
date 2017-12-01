/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mavha.cap.java.aplicacion.productora;

import com.mavha.cap.java.aplicacion.productora.modelo.Pedido;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.Queue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author martdominguez
 */
@RequestScoped
@Named("productor")
@JMSDestinationDefinition(
        name = "cola1",
        interfaceName = "javax.jms.Queue")
public class ProductorMensajes {

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(lookup = "cola1")
    private static Queue queue;

    private Integer valor;
    private String texto;

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void doEnviarMensaje() {
        System.out.println(" MENSAJE ENVIADO!!!");
        //        context.createProducer().send(queue, "a:"+this.texto+";b"+this.valor);
        try (JMSContext context1 = connectionFactory.createContext();) {
            context1.createProducer().send(queue, "a:" + this.texto + ";b" + this.valor);
        }
    }
    
    
}
