import App from '@/App';
import Header from '@/components/core/header/header';
import { LandingPage } from '@/pages/landing/landing';
import { createBrowserRouter } from 'react-router-dom';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    // TODO: errorElement: (),
    children: [
      {
        index: true,
        element: (
          <>
            <Header />
            <LandingPage />
          </>
        ),
      },
    ],
  },
]);
