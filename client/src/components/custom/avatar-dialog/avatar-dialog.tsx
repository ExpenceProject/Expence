import { Avatar } from '@/components/ui/avatar';
import { useColorModeValue } from '@/components/ui/color-mode';
import {
  DialogBackdrop,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogHeader,
  DialogRoot,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  PopoverArrow,
  PopoverBody,
  PopoverContent,
  PopoverRoot,
  PopoverTitle,
  PopoverTrigger,
} from '@/components/ui/popover';
import { User } from '@/types';
import {
  closeAvatarModalAtom,
  isAvatarModalOpenAtom,
} from '@/utils/atoms/modal-atoms';
import { Button, Flex, Input, Text } from '@chakra-ui/react';
import { AxiosResponse } from 'axios';
import { useAtom } from 'jotai';
import { FC, useMemo, useRef, useState } from 'react';
import { FiUpload } from 'react-icons/fi';
import { IoMdCheckmark } from 'react-icons/io';
import { MdDelete } from 'react-icons/md';
import { toast } from 'react-toastify';

type AvatarDialogProps = {
  user?: User;
  updateUserAvatar: (image: FormData) => Promise<AxiosResponse>;
  deleteUserAvatar: () => Promise<AxiosResponse>;
  refreshUser: () => Promise<User>;
};

export const AvatarDialog: FC<AvatarDialogProps> = ({
  user,
  updateUserAvatar,
  deleteUserAvatar,
  refreshUser,
}) => {
  const currentTheme = useColorModeValue('light', 'dark');
  const [isAvatarModalOpen] = useAtom(isAvatarModalOpenAtom);
  const [uploadedImage, setUploadedImage] = useState<File | null>(null);
  const [isAlertDialogOpen, setIsAlertDialogOpen] = useState<boolean>(false);
  const [, closeAvatarModal] = useAtom(closeAvatarModalAtom);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleAvatarUpdate = () => {
    if (uploadedImage) {
      const formData = new FormData();
      formData.append('image', uploadedImage);

      updateUserAvatar(formData)
        .then(async () => {
          await refreshUser();
          toast.success('Avatar updated successfully');
        })
        .catch((error) => {
          console.error(error);
          toast.error(
            'Failed to update avatar, please try again in a few minutes',
          );
        })
        .finally(() => {
          closeAvatarModal();
          setUploadedImage(null);
        });
    } else {
      closeAvatarModal();
    }
  };

  const handleAvatarDelete = () => {
    deleteUserAvatar()
      .then(async () => {
        await refreshUser();
        toast.success('Avatar deleted successfully');
      })
      .catch((error) => {
        console.error(error);
        toast.error(
          'Failed to delete avatar, please try again in a few minutes',
        );
      })
      .finally(() => {
        closeAvatarModal();
        setUploadedImage(null);
      });
  };

  const handleInputClick = () => {
    if (inputRef.current) {
      inputRef.current.click();
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (files && files.length > 0) {
      const file = files[0];
      setUploadedImage(file);
    }
  };

  const userAvatarSource = useMemo(
    () =>
      uploadedImage
        ? URL.createObjectURL(uploadedImage)
        : user?.image.key &&
          `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${user.image.key}`,
    [user, uploadedImage],
  );

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isAvatarModalOpen}
      onInteractOutside={closeAvatarModal}
      onEscapeKeyDown={closeAvatarModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogContent shadow="xl" p="4">
        <DialogCloseTrigger _icon={{ w: 6, h: 6 }} onClick={closeAvatarModal} />
        <DialogHeader>
          <DialogTitle fontSize="xl">Change Your Photo</DialogTitle>
        </DialogHeader>
        <DialogBody
          data-theme={currentTheme}
          display="flex"
          alignItems="center"
          justifyContent="center"
          flexDirection="column"
          p={6}
        >
          <Avatar
            src={userAvatarSource}
            variant="solid"
            name={`${user?.firstName} ${user?.lastName}`}
            color="textBg"
            bg="primary"
            nameSize={45}
            contain="content"
            w="200px"
            h="200px"
            className="modalAvatar"
            outlineWidth={4}
            outlineColor="primary"
            outlineOffset={-1}
            outlineStyle="solid"
          />
          <Flex
            w="100%"
            justifyContent="center"
            alignItems="center"
            direction={'column'}
            pt={10}
            gap={10}
          >
            {uploadedImage && (
              <Text truncate maxW="80%">
                Source: {uploadedImage.name}
              </Text>
            )}
            <Flex
              w="100%"
              justifyContent="center"
              alignItems="center"
              gap={{ base: 4, md: 8 }}
            >
              <Button
                display="flex"
                alignItems="center"
                justifyContent="center"
                flex={1}
                color="textBg"
                _hover={{ bg: 'hoverPrimary' }}
                bgColor="primary"
                fontSize={{ base: 'sm', md: 'md' }}
                onClick={handleAvatarUpdate}
              >
                <IoMdCheckmark />
                <Text display={{ base: 'none', sm: 'unset' }}>Save</Text>
              </Button>
              <Button
                display="flex"
                alignItems="center"
                justifyContent="center"
                flex={1}
                color="textBg"
                _hover={{ bg: 'hoverPrimary' }}
                bgColor="primary"
                fontSize={{ base: 'sm', md: 'md' }}
                onClick={handleInputClick}
              >
                <FiUpload />{' '}
                <Text display={{ base: 'none', sm: 'unset' }}>Upload</Text>
              </Button>
              <Input
                type="file"
                ref={inputRef}
                display="none"
                onChange={handleFileChange}
                accept="image/*"
                max={1}
              />
              <PopoverRoot
                open={isAlertDialogOpen}
                onOpenChange={(e) => setIsAlertDialogOpen(e.open)}
                positioning={{
                  placement: 'bottom-end',
                  offset: { crossAxis: 0, mainAxis: 46 },
                }}
              >
                <PopoverTrigger asChild>
                  <Button
                    display="flex"
                    alignItems="center"
                    justifyContent="center"
                    flex={1}
                    variant="outline"
                    outlineColor="textError"
                    _hover={{ bg: 'backgroundErrorHover' }}
                    color="textError"
                    bgColor="backgroundError"
                    fontSize={{ base: 'sm', md: 'md' }}
                  >
                    <MdDelete />
                    <Text display={{ base: 'none', sm: 'unset' }}>Delete</Text>
                  </Button>
                </PopoverTrigger>
                <PopoverContent>
                  <PopoverArrow position={'top-end'} />
                  <PopoverBody>
                    <PopoverTitle>
                      Are you sure you want to delete your avatar?
                    </PopoverTitle>
                    <Flex gap={4} pt={4}>
                      <Button
                        flex={1}
                        color="textError"
                        _hover={{ bg: 'backgroundErrorHover' }}
                        bgColor="backgroundError"
                        fontSize={{ base: 'sm', md: 'md' }}
                        onClick={handleAvatarDelete}
                      >
                        Yes
                      </Button>
                      <Button
                        flex={1}
                        color="textBg"
                        _hover={{ bg: 'hoverPrimary' }}
                        bgColor="primary"
                        fontSize={{ base: 'sm', md: 'md' }}
                        onClick={() => setIsAlertDialogOpen(false)}
                      >
                        Cancel
                      </Button>
                    </Flex>
                  </PopoverBody>
                </PopoverContent>
              </PopoverRoot>
            </Flex>
          </Flex>
        </DialogBody>
      </DialogContent>
    </DialogRoot>
  );
};
