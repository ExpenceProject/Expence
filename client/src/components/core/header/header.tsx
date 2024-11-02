import Drawer from '@/components/custom/drawer/drawer';
import Logo from '@/components/custom/logo/logo';
import { ColorModeButton } from '@/components/ui/color-mode';
import { maxWebsiteWidth } from '@/style/variables';
import {
  Box,
  Button,
  Flex,
  Link,
  Span,
  useBreakpointValue,
} from '@chakra-ui/react';

import HeaderNav from './header-nav';

const Header = () => {
  const currentBreakpoint = useBreakpointValue({
    sm: 'sm',
    md: 'md',
    lg: 'lg',
    xl: 'xl',
  });

  console.log(!currentBreakpoint);

  return (
    <Box
      as="header"
      bg="surface"
      display="flex"
      justifyContent="center"
      alignItems="center"
      p={4}
      w="100%"
    >
      <Flex
        w="100%"
        maxW={maxWebsiteWidth}
        justify="space-between"
        align="center"
      >
        <Link
          color="textRaw"
          textDecor="none"
          href="/"
          _focus={{ outline: 'none' }}
        >
          <Flex align="center" justify="center" gap="1">
            <Logo fill="primary" />
            <Span fontSize="2xl" color="textRaw" fontWeight="bold">
              Expence
            </Span>
          </Flex>
        </Link>
        <Flex align="center" justify="center" gap="2" md={{ gap: '6' }}>
          {currentBreakpoint === 'sm' || !currentBreakpoint ? (
            <Drawer />
          ) : (
            <HeaderNav />
          )}
          <ColorModeButton />
          <Button
            fontSize="lg"
            bg="primary"
            color="textBg"
            _hover={{ bg: 'hover' }}
          >
            Sign Up
          </Button>
        </Flex>
      </Flex>
    </Box>
  );
};

export default Header;
