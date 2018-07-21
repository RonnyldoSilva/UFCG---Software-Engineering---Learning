/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.systematictesting.automation.core.framework.impl;

import javax.swing.JTextArea;

import com.systematictesting.automation.core.framework.TraceHandler;

/**
 *
 * @author sharad
 */
public class SwingLoggingBasedTraceHandler implements TraceHandler{
    
    private JTextArea loggerTextArea;
    
    public SwingLoggingBasedTraceHandler(JTextArea loggerTextArea){
        this.loggerTextArea = loggerTextArea;
    }

    @Override
    public void logEvents(String log) {
        if (this.loggerTextArea!=null){
            this.loggerTextArea.append(log+" \n");
            this.loggerTextArea.setCaretPosition(loggerTextArea.getDocument().getLength());
        }
    }
    
}
