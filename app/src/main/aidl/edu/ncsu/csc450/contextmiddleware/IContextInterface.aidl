package edu.ncsu.csc450.contextmiddleware;

import edu.ncsu.csc450.contextmiddleware.IContextCallback;

/*
 * The ILocationInterface is to be implemented in th middleware. There are three types of implementations that are demonstrated:
 * 1. By using a return type, the function will work in a synchronous manner.
 * 2. Without using a return type and sending a callback interface as parameter, the function can work asynchronously. However, the method in the
 *    callback that is to be invoked upon completion of execution is to be known for this to work.
 *
 *    The method registerForLocationUpdates() is implemented in such a way that it continuously updates the application whenever a change in location
 *    is detected.
 * 3. The method notifyAtNewYork() only invokes the callback if the user's location is detected to be within 20 miles of New York City. This method
 *    only invokes the application once.
 */
interface IContextInterface {

    void registerForLocationUpdates(IContextCallback callback);

    void getBatteryUpdates(IContextCallback callback);

    // This method cannot be tested in an emulator. Use actual device for testing
    boolean isJackPluggedIn();

}
