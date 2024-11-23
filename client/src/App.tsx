import { Outlet } from 'react-router';
import { ToastContainer } from 'react-toastify';

import Header from './components/core/header/header';
import LoginDialog from './components/custom/login-dialog/login-dialog';
import RegisterDialog from './components/custom/register-dialog/register-dialog';
import { useUser } from './utils/providers/user-provider/use-user';

function App() {
  const { user } = useUser();

  return (
    <>
      <Header />
      <ToastContainer />
      <Outlet />
      {!user && (
        <>
          <LoginDialog />
          <RegisterDialog />
        </>
      )}
    </>
  );
}

export default App;
