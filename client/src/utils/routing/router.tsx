import App from '@/App';
import { LandingPage } from '@/pages/landing/landing';
import ProfilePage from '@/pages/landing/profile/profile';
import { PageNotFound } from '@/pages/page-not-found/page-not-found';
import { createBrowserRouter } from 'react-router-dom';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    // TODO: errorElement: (),
    children: [
      {
        index: true,
        element: <LandingPage />,
      },
      {
        path: '/profile',
        element: <ProfilePage />,
      },
    ],
  },
  {
    path: '*',
    element: <PageNotFound />,
  },
]);
