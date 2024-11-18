import { ColorModeProvider } from '@/components/ui/color-mode';
import { theme } from '@/style/theme/theme';
import { UserProvider } from '@/utils/providers/user-provider/user-provider';
import { ChakraProvider } from '@chakra-ui/react';
import { FC, ReactNode } from 'react';

export const AppProvider: FC<{ children: ReactNode }> = ({ children }) => {
  return (
    <ChakraProvider value={theme}>
      <ColorModeProvider storageKey="theme">
        <UserProvider>{children}</UserProvider>
      </ColorModeProvider>
    </ChakraProvider>
  );
};
