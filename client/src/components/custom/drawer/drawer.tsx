import HeaderNav from '@/components/core/header/header-nav';
import { BurgerIcon } from '@/components/icons/burger';
import { CloseTriggerIcon } from '@/components/icons/close-trigger';
import { useColorModeValue } from '@/components/ui/color-mode';
import {
  DrawerBackdrop,
  DrawerBody,
  DrawerCloseTrigger,
  DrawerContent,
  DrawerHeader,
  DrawerRoot,
  DrawerTrigger,
  useBreakpointValue,
  useDisclosure,
} from '@chakra-ui/react';

export const Drawer = () => {
  const { open, onOpen, onClose } = useDisclosure();

  const currentTheme = useColorModeValue('light', 'dark');

  const currentBreakpoint = useBreakpointValue({
    sm: 'sm',
    md: 'md',
    lg: 'lg',
    xl: 'xl',
  });

  if (currentBreakpoint && currentBreakpoint !== 'sm') {
    return;
  }

  return (
    <DrawerRoot open={open} placement="end" onEscapeKeyDown={onClose}>
      <DrawerBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
        // These two props below are necessary for the backdrop to close the drawer, it's probably a bug in Chakra UI
        onClick={onClose}
        pointerEvents="auto"
      />
      <DrawerTrigger>
        <BurgerIcon
          onClick={onOpen}
          fill={currentTheme === 'light' ? 'black' : 'white'}
        />
      </DrawerTrigger>
      <DrawerContent h="100vh" position="fixed" top={0} right={0} bg="surface">
        <DrawerHeader py="7">
          <CloseTriggerIcon onClick={onClose} />
        </DrawerHeader>
        <DrawerBody>
          <HeaderNav direction="column" closeDrawer={onClose} />
        </DrawerBody>
        <DrawerCloseTrigger />
      </DrawerContent>
    </DrawerRoot>
  );
};
