package edu.bulletin.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IStore extends Remote {
    ReaderResult readNews(int clientId) throws RemoteException;
    WriterResult write(int newsValue) throws RemoteException;
}
