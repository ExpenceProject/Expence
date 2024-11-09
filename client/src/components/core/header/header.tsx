import Drawer from '@/components/custom/drawer/drawer';
import Logo from '@/components/custom/logo/logo';
import { ColorModeButton } from '@/components/ui/color-mode';
import {
  coreMobilePaddingX,
  corePaddingX,
  maxWebsiteWidth,
} from '@/style/variables';
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

  return (
    <Box
      as="header"
      bg="background"
      display="flex"
      justifyContent="center"
      alignItems="center"
      px={{ base: coreMobilePaddingX, md: corePaddingX }}
      py={4}
      w="100%"
      borderBottom="1px solid rgba(161, 161, 161, 0.3)"
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
            <Logo fill="primary" width={35} />
            <Span fontSize="2xl" color="textRaw" fontWeight="bold">
              Expence
            </Span>
          </Flex>
        </Link>
        <Flex align="center" justify="center" gap={{ base: 2, md: 6 }}>
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
