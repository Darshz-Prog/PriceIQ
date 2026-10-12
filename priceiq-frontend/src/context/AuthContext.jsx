import { createContext, useContext, useMemo, useState } from "react";
import { clearSession, getSession, saveSession } from "../api/client";
const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [user, setUser] = useState(getSession);
  const signIn = (auth) => {
    saveSession(auth);
    setUser(getSession());
  };
  const signOut = () => {
    clearSession();
    setUser(null);
  };
  const value = useMemo(() => ({ user, signIn, signOut }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export const useAuth = () => useContext(AuthContext);
