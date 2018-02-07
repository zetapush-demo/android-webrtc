package com.zetapush.library;

/**
 * Created by mikaelmorvan on 23/01/2018.
 */

import java.util.Map;

/**
 * Callback interface for ZetaPush connection.
 *
 */
public interface ZetaPushConnectionEvent {

    void successfulHandshake(Map<String, Object> map);

    void failedHandshake(Map<String, Object> map);

    void connectionEstablished();

    void connectionBroken();

    void connectionClosed();

    void messageLost(String s, Object o);
}
